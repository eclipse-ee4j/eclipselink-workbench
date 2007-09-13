package defaultPackage;

public interface PurchaseOrderType {

   public java.lang.String getPoId();

   public void setPoId(java.lang.String value);

   public defaultPackage.AddressType getShipTo();

   public void setShipTo(defaultPackage.AddressType value);

   public defaultPackage.AddressType getBillTo();

   public void setBillTo(defaultPackage.AddressType value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public defaultPackage.Items getItems();

   public void setItems(defaultPackage.Items value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}
