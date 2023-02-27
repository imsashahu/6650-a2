public class SwipeDetails {

    private int swiper;
    private int swipee;
    private String comment;
    private String direction;

    public SwipeDetails(int swiper, int swipee, String comment, String direction) {
        this.swiper = swiper;
        this.swipee = swipee;
        this.comment = comment;
        this.direction = direction;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String comment) {
        this.direction = direction;
    }
}
