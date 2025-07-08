import java.util.Map;
import java.util.HashMap;

abstract class Product {
  protected String id;
  protected String name;
  protected double price;
  
  public Product(String id, String name, double price) {
    this.id = id;
    this.name = name;
    this.price = price;
  }
  
  public String getId() { return id; }
  public String getName() { return name; }
  public double getPrice() { return price; }
  
  public abstract boolean isAvailable();
  public abstract void reduceStock(int qty);
}

abstract class Book extends Product {
  protected int publishYear;
  
  public Book(String ISDN, String title, double price, int publishYear) { 
    super(ISDN, title, price);
    this.publishYear = publishYear;
  }
  
  public String getISDN() { return id; }
  public int getPublishYear() { return publishYear; }
}

interface Shippable {
  void shipTo(String address);
}

interface Emailable {
  void sendTo(String email);
}

class ShippingService {
  public static void send(Product product, String address) {
    System.out.println("Shipping " + product.getName() + " To " + address);
  }
}

class EmailService {
  public static void send(Product product, String email) {
    System.out.println("Emailing " + product.getName() + " To " + email);
  }
}

class PaperBook extends Book implements Shippable {
  private int stock;
  
  public PaperBook(String ISDN, String title, int price, int publishYear, int stock) {
    super(ISDN, title, price, publishYear);
    this.stock = stock;
  }
  
  @Override
  public boolean isAvailable() { return stock > 0; }
  
  @Override
  public void reduceStock(int qty) {
    if(qty > stock) throw new IllegalArgumentException("Stock is not enuogh!");
    stock -= qty;
  }
  
  @Override
  public void shipTo(String address) {
    ShippingService.send(this, address);
  }
}

class EBook extends Book implements Emailable {
  private String fileType;
  
  public EBook(String ISDN, String title, int price, int publishYear, String fileType) {
    super(ISDN, title, price, publishYear);
    this.fileType = fileType;
  }
  
  @Override
  public boolean isAvailable() { return true; }
  
  @Override
  public void reduceStock(int qty) { }
  
  @Override
  public void sendTo(String email) {
    EmailService.send(this, email);
  }
}

class DemoBook extends Book {
  public DemoBook(String ISDN, String title, int price, int publishYear) {
    super(ISDN, title, price, publishYear);
  }
  
  @Override
  public boolean isAvailable() { return false; }
  
  @Override
  public void reduceStock(int qty) {
    throw new UnsupportedOperationException("Demo Books are not for sale!");
  }
}

class BookStore {
  private Map<String, Product> inventory = new HashMap<>();
  
  public void addProduct(Product product) {
    inventory.put(product.getId(), product);
  }
  
  public void removeOutdatedBooks(int threshold) {
    inventory.values().removeIf(p -> 
    (p instanceof Book) && ((Book) p).getPublishYear() < threshold
    );
  }
  
  public void buyProduct(String id, int qty, String address, String email) {
    Product product = inventory.get(id);
    if(product == null) throw new IllegalArgumentException("Product not found!");
    if(!product.isAvailable()) throw new IllegalArgumentException("Product is not available for sale.");
    
    product.reduceStock(qty);
    System.out.println(qty + "x " + product.getName() + "     " + product.getPrice() * qty);
    
    if(product instanceof Shippable) {
      ((Shippable) product).shipTo(address);
    }
    
    if(product instanceof Emailable) {
      ((Emailable) product).sendTo(email);
    }
  }
  
  public void printInventory() {
    System.out.println("** Inventory **");
    for(Product product : inventory.values()) {
      System.out.println(product.getId() + " - " + product.getName() + " - " + product.getPrice());
    }
  }
}

class Main {
  public static void main(String[] args) {
        BookStore store = new BookStore();

        store.addProduct(new PaperBook("123", "Java 8 intro", 150, 2015, 5));
        store.addProduct(new EBook("456", "Angular Basics", 100, 2022, "PDF"));
        store.addProduct(new DemoBook("789", "Linux Introduction", 0, 1999));

        store.printInventory();

        System.out.println("\nBuying Paper Book:");
        store.buyProduct("123", 1, "Cairo, Egypt", "user@mail.com");

        System.out.println("\nBuying EBook:");
        store.buyProduct("456", 1, "",  "user@mail.com");

        System.out.println("\nBuying Showcase Book:");
        store.buyProduct("789", 1, "", "user@mail.com");

        System.out.println("\nRemoving books older than year 2000:");
        store.removeOutdatedBooks(2000);

        System.out.println("\nUpdated Inventory:");
        store.printInventory();
  }
}
