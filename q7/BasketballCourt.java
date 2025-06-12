public class BasketballCourt {
    public synchronized void goToCourt(String gang) {
        System.out.println(gang + " goes to the court.");
    }

    public synchronized void leaveCourt(String gang) {
        System.out.println(gang + " leaves the court.");
    }
}