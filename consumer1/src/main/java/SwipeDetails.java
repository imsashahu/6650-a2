public class SwipeDetails {

    private int swiper;
    private int swipee;
    private String comment;

    public SwipeDetails(int swiper, int swipee, String comment) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
    }

    public int getSwiper() {
        return swiper;
    }

    public void setSwiper(int swiper) {
        this.swiper = swiper;
    }

    public int getSwipee() {
        return swipee;
    }

    public void setSwipee(int swipee) {
        this.swipee = swipee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
