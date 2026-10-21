package core.basesyntax.service.impl;

import core.basesyntax.db.FruitStorage;
import core.basesyntax.model.FruitTransaction;
import core.basesyntax.service.ShopService;
import core.basesyntax.strategy.OperationStrategy;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopServiceImpl implements ShopService {
    private final OperationStrategy operationStrategy;
    private final FruitStorage fruitStorage;

    public ShopServiceImpl(OperationStrategy operationStrategy, FruitStorage fruitStorage) {
        this.operationStrategy = operationStrategy;
        this.fruitStorage = fruitStorage;
    }

    @Override
    public void process(List<FruitTransaction> fruitTransactions) {
        if (fruitTransactions.isEmpty()) {
            throw new IllegalArgumentException("List of transactions must not be empty");
        }

        fruitTransactions.stream()
                .collect(Collectors.groupingBy(FruitTransaction::getFruitName))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> calculateBalance(e.getValue())))
                .forEach(fruitStorage::update);
    }

    private Integer calculateBalance(List<FruitTransaction> fruitTransactions) {
        int balance = 0;
        for (FruitTransaction fruitTransaction: fruitTransactions) {
            balance = operationStrategy.getOperationHandler(fruitTransaction.getOperation())
                   .updateQuantity(balance, fruitTransaction.getQuantity());
        }

        if (balance < 0) {
            throw new RuntimeException("Balance cannot be negative");
        }

        return balance;
    }
}
