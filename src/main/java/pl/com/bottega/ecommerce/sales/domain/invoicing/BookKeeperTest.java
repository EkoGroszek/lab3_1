package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class BookKeeperTest {

    private InvoiceFactory invoiceFactory;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData;
    private TaxPolicy taxPolicy;
    private Money money;
    private RequestItem requestItem;
    private ProductData productData;
    private Tax tax;
    private BookKeeper bookKeeper;

    @Before public void init() {
        clientData = new ClientData(Id.generate(), "clientNameStub");
        invoiceRequest = new InvoiceRequest(clientData);
        money = Money.ZERO;
        productData = new ProductData(Id.generate(), money, "productNameStub", ProductType.STANDARD, new Date());
        requestItem = new RequestItem(productData, 0, money);
        invoiceFactory = new InvoiceFactory();
        tax = new Tax(money, "taxStub");
        taxPolicy = mock(TaxPolicy.class);
        bookKeeper = new BookKeeper(invoiceFactory);
        when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);
    }

    @Test public void oneRequestItemShouldReturnInvoiceWithOneItem() {
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(1));
    }

    @Test public void noRequestItemShouldReturnInvoiceWithNoItems() {
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(0));
    }

    @Test public void noRequestItemShouldReturnInvoiceShouldreturnInvoiceWithNetEqualsZero() {
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getNet(), is(Money.ZERO));
    }

    @Test public void twoRequestItemsShouldCallCalculateTaxTwice() {
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(2)).calculateTax(any(), any());
    }

    @Test public void noRequestItemsShouldNotCallCalculateTax() {
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(0)).calculateTax(any(), any());
    }

    @Test public void twoInvoicesWithOneItemEachShouldCallCalculateTaxTwice() {
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(2)).calculateTax(any(), any());
    }
}
