package tv.quaint.utilitybase.config.storage;

public class StorageComponent {
    public String key;
    public double value;

    public StorageComponent(String key, double value){
        this.key = key;
        this.value = value;
    }

    public StorageComponent addAmount(double amount) {
        this.value += amount;
        StorageManager.saveComponent(this);

        return this;
    }

    public StorageComponent removeAmount(double amount) {
        this.value -= amount;
        StorageManager.saveComponent(this);

        return this;
    }

    public StorageComponent setAmount(double amount) {
        this.value = amount;
        StorageManager.saveComponent(this);

        return this;
    }
}
