package payloads;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class BookingDates {

    @JsonProperty
    private String checkin;
    @JsonProperty
    private String checkout;

    public String getCheckin() {
        return checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    // default constructor required by Jackson
    private BookingDates() {
        // nothing here
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public static class Builder {

        private String checkin;
        private String checkout;

        public Builder setCheckin(String checkin){
            this.checkin = checkin;
            return this;
        }

        public Builder setCheckout(String checkout){
            this.checkout = checkout;
            return this;
        }

        public BookingDates build() {
            return new BookingDates(checkin, checkout);
        }
    }
}
