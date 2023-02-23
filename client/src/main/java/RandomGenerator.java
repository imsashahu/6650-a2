import java.util.Random;

public class RandomGenerator {
    private String swipe;
    private String swiper;
    private String swipee;
    private String comment;

    public RandomGenerator() {
        this.swipe = null;
        this.swiper = null;
        this.swipee = null;
        this.comment = null;
    }

    public void generate() {
        int randomSwipe = new Random().nextInt(1+1);
        this.swipe = ((randomSwipe == 0) ? "left" : "right");

        this.swiper = String.valueOf(new Random().nextInt(5000) + 1);
        this.swipee = String.valueOf(new Random().nextInt(1000000) + 1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i ++) {
            int randomIndex = new Random().nextInt(26);
            sb.append((char) (randomIndex + 'A'));
        }
        this.comment = sb.toString();
    }

    public String getSwipe() {
        return swipe;
    }

    public String getSwiper() {
        return swiper;
    }

    public String getSwipee() {
        return swipee;
    }

    public String getComment() {
        return comment;
    }
}
