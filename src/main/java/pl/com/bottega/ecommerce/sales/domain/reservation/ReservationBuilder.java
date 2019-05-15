package pl.com.bottega.ecommerce.sales.domain.reservation;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.Date;
import java.util.List;

public class ReservationBuilder {

    private Reservation.ReservationStatus status;

    private List<ReservationItem> items;

    private ClientData clientData;

    private Date createDate;

    public ReservationBuilder() {
    }

    public ReservationBuilder setStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder setItems(List<ReservationItem> items) {
        this.items = items;
        return this;
    }

    public ReservationBuilder setClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation build(){
        return new Reservation(Id.generate(), status, clientData, createDate);
    }
}
