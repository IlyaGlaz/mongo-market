
public class MongoRunner {

    private static final String ADD_M = "ADD_MARKET";
    private static final String ADD_P = "ADD_PRODUCT";
    private static final String SELL = "SELL_PRODUCT";
    private static final String SHOW = "SHOW_INFO";

    public static void main(String[] args) {
        MongoBase base = new MongoBase();

        try {
            for (; ; ) {
                String userInput = UserInput.getLine();
                if (userInput.startsWith(ADD_M)) {
                    base.addMarket(userInput.replaceFirst(ADD_M, "").trim());
                } else if (userInput.startsWith(ADD_P)) {
                    base.addGoods(userInput.replaceFirst(ADD_P, "").trim());
                } else if (userInput.startsWith(SELL)) {
                    base.saleGoods(userInput.replaceFirst(SELL, "").trim());
                } else if (userInput.equals(SHOW)) {
                    base.showInfo();
                } else {
                    System.out.println("Wrong format of command");
                }
            }
        } finally {
            base.close();
        }
    }
}
