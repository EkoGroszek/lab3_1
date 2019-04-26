package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class AddProductCommandHandlerTest {

    private AddProductCommand addProductCommand;
    private Id orderId;
    private Id productId;

    private ReservationRepository reservationRepository;

    private ProductRepository productRepository;

    private SuggestionService suggestionService;

    private ClientRepository clientRepository;

    private SystemContext systemContext;

    private AddProductCommandHandler addProductCommandHandler;

    private Reservation reservation;

    private Product product;

    private Client client;

    @Before public void init() {
        orderId = Id.generate();
        productId = Id.generate();
        addProductCommand = new AddProductCommand(orderId, productId, 2);
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                                                                clientRepository, systemContext);
        reservation = new Reservation(orderId, Reservation.ReservationStatus.OPENED, new ClientData(), new Date());
        product = new Product(productId, Money.ZERO, "productNameStub", ProductType.STANDARD);
        client = new Client();
    }

    @Test public void oneCallHandleShouldReturnReservationWithOneItem() {
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        Assert.assertThat(reservation.getReservedProducts().size(), is(1));
    }

    @Test public void noCallHandleShouldReturnReservationWithNo() {
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);

        Assert.assertThat(reservation.getReservedProducts().size(), is(0));
    }

    @Test public void oneCallHandleShouldLoadReservationRepositoryOnce() {
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository, times(1)).load(any());
    }

    @Test public void oneCallHandleShouldLoadProductOnce() {
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(productRepository, times(1)).load(any());
    }


    @Test public void oneCallHandleShouldSaveReservationRepositoryOnce() {
        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository, times(1)).save(any());
    }
}
