package jent.fun_with_interviews.misc;

public class FinalMemberInitialization {

    private final ReservationComponent reservationComponent; // cannot assign to null if we want to assign it to an acutal instance.

    FinalMemberInitialization(ReservationComponent reservationComponent) {
        this.reservationComponent = reservationComponent;
    }

    static class ReservationComponent {
        private String name;
    }
}
