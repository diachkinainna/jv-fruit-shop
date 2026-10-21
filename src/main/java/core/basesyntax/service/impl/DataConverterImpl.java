package core.basesyntax.service.impl;

import core.basesyntax.model.FruitTransaction;
import core.basesyntax.service.DataConverter;
import java.util.List;
import java.util.Optional;

public class DataConverterImpl implements DataConverter {
    private static final int TRANSACTION_PARTS_LENGTH = 3;
    private static final int CODE_INDEX = 0;
    private static final int FRUIT_NAME_INDEX = 1;
    private static final int QUANTITY_INDEX = 2;
    private static final int HEADER_ROW_INDEX = 0;
    private static final String DELIMITER = ",";

    @Override
    public List<FruitTransaction> convertToTransactions(List<String> transactions) {
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("Transaction data is empty");
        }

        return transactions.stream()
                .skip(HEADER_ROW_INDEX)
                .map(this::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<FruitTransaction> parse(String transaction) {
        String[] transactionParts = transaction.split(DELIMITER);

        if (transactionParts.length != TRANSACTION_PARTS_LENGTH) {
            return Optional.empty();
        }

        try {
            FruitTransaction fruitTransaction = new FruitTransaction();
            fruitTransaction.setOperation(FruitTransaction.Operation.fromCode(
                    transactionParts[CODE_INDEX]));
            fruitTransaction.setFruitName(transactionParts[FRUIT_NAME_INDEX]);
            int quantity = Integer.parseInt(transactionParts[QUANTITY_INDEX]);

            if (quantity < 0) {
                throw new RuntimeException("quantity can't be less than zero");
            }

            fruitTransaction.setQuantity(quantity);
            return Optional.of(fruitTransaction);

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
