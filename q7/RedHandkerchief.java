public class RedHandkerchief {
    private boolean isRedTied = false; // Initially no red handkerchief tied

    public synchronized boolean isRedTied() {
        return isRedTied;
    }

    public synchronized void tieRed() {
        this.isRedTied = true;
    }

    public synchronized void untieRed() {
        this.isRedTied = false;
    }
}