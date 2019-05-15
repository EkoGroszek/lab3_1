package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {
    private Money price = Money.ZERO;

    private String name = "productNameStub";

    private ProductType productType = ProductType.STANDARD;

    public ProductBuilder(){

    }

    public ProductBuilder setPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public Product build(){
        return new Product(Id.generate(),price,name,productType);
    }

}
