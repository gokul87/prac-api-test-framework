package payloads;

import org.codehaus.jackson.annotate.JsonProperty;

public class Booking {

    @JsonProperty
    private String firstname;
    @JsonProperty
    private String lastname;
    @JsonProperty
    private int totalprice;
    @JsonProperty
    private boolean depositpaid;
    @JsonProperty
    private String additionalneeds;
    @JsonProperty
    private BookingDates bookingdates;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public boolean getDepositpaid(){
        return depositpaid;
    }

    public String getAdditionalneeds(){
        return additionalneeds;
    }

    public BookingDates getBookingdates(){
        return bookingdates;
    }

    // default constructor required by Jackson
    private Booking() {
        // nothing here
    }

    public Booking(String firstname, String lastname, int totalprice, boolean depositpaid, String additionalneeds, BookingDates bookingDates){
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.additionalneeds = additionalneeds;
        this.bookingdates = bookingDates;
    }

    public static class Builder {

        private String firstname;
        private String lastname;
        private int totalprice;
        private boolean depositpaid;
        private String additionalneeds;
        private BookingDates bookingdates;

        public Builder setFirstname(String firstname){
            this.firstname = firstname;
            return this;
        }

        public Builder setLastname(String lastname){
            this.lastname = lastname;
            return this;
        }

        public Builder setTotalprice(int totalprice){
            this.totalprice = totalprice;
            return this;
        }

        public Builder setDepositpaid(boolean depositpaid){
            this.depositpaid = depositpaid;
            return this;
        }

        public Builder setAdditionalneeds(String additionalneeds){
            this.additionalneeds = additionalneeds;
            return this;
        }

        public Builder setBookingdates(BookingDates bookingdates) {
            this.bookingdates = bookingdates;
            return this;
        }

        public Booking build(){
            return new Booking(firstname, lastname, totalprice, depositpaid, additionalneeds, bookingdates);
        }
    }
}
