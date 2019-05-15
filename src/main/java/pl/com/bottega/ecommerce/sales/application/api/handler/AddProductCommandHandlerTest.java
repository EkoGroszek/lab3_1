package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationBuilder;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

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

    private ProductBuilder productBuilder;

    private ReservationBuilder reservationBuilder;

    private AddProductCommandHandlerBuilder addProductCommandHandlerBuilder;

    @Before public void init() {
        orderId = Id.generate();
        productId = Id.generate();
        addProductCommand = new AddProductCommand(orderId, productId, 2);
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);

        addProductCommandHandlerBuilder = new AddProductCommandHandlerBuilder();
        addProductCommandHandler = addProductCommandHandlerBuilder.setReservationRepository(reservationRepository)
                                                                  .setProductRepository(productRepository)
                                                                  .setSuggestionService(suggestionService)
                                                                  .setClientRepository(clientRepository)
                                                                  .build();
        reservationBuilder = new ReservationBuilder();
        reservation = reservationBuilder.build();

        productBuilder = new ProductBuilder();
        product = productBuilder.build();
        client = new Client();

        when(reservationRepository.load(any())).thenReturn(reservation);
        when(productRepository.load(any())).thenReturn(product);
        when(clientRepository.load(any())).thenReturn(client);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
    }

    @Test public void oneCallHandleShouldReturnReservationWithOneItem() {
        addProductCommandHandler.handle(addProductCommand);

        Assert.assertThat(reservation.getReservedProducts().size(), is(1));
    }

    @Test public void noCallHandleShouldReturnReservationWithNo() {
        Assert.assertThat(reservation.getReservedProducts().size(), is(0));
    }

    @Test public void oneCallHandleShouldLoadReservationRepositoryOnce() {
        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository, times(1)).load(any());
    }

    @Test public void oneCallHandleShouldLoadProductOnce() {
        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(productRepository, times(1)).load(any());
    }

    @Test public void oneCallHandleShouldSaveReservationRepositoryOnce() {
        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(reservationRepository, times(1)).save(any());
    }

    @Test public void whenProductIsNotAvailableClientShouldNotBeLoaded() {
        BaseAggregateRoot baseAggregateRoot = mock(BaseAggregateRoot.class);
        when(baseAggregateRoot.isRemoved()).thenReturn(true);
        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(suggestionService, times(0)).suggestEquivalent(any(), any());
    }
}
