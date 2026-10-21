import java.util.Arrays;

public  class AnnotatedClass {

    public void publicHello(String name) {
        System.out.println("  publicHello: Привет, " + name);
    }

    @Repeat(2)
    public void publicSay(String text) {
        System.out.println("  publicSay: " + text);
    }

    public void publicSayGoodby() {
        System.out.println("  publicHello: Пока!");
    }



    @Repeat(3)
    protected double protectedMultiply(int a, double b) {
        System.out.println("  protectedMultiply: " + a + " * " + b + " = " + (a * b));
        return a * b;
    }

    @Repeat(2)
    protected void protectedGreet(String text, int count, boolean loud) {
        String msg = text.repeat(count);
        System.out.println("  protectedGreet: " + (loud ? msg.toUpperCase() : msg));
    }

    @Repeat(2)
    protected Point protectedMove(Point point, int dx) {
        Point moved = point.move(dx, dx);
        System.out.println("  protectedMove: " + point + " -> " + moved);
        return moved;
    }

    protected void protectedNotAnnotated(long x) {
        System.out.println("  protectedNotAnnotated: " + x);
    }




    @Repeat(2)
    private String privateSecret(String code) {
        System.out.println("  privateSecret: код = " + code);
        return "secret-" + code;
    }

    @Repeat(1)
    private void privateManyParams(char c, String[] texts, int[] numbers) {
        System.out.println("  privateManyParams: c=" + c
                + ", texts.length=" + texts.length
                + ", numbers.length=" + numbers.length);
    }

    @Repeat(2)
    private void privateIntroduce(Person person, Color color, Point[] points) {
        System.out.println("  privateIntroduce: " + person + ", цвет=" + color
                + ", points=" + Arrays.toString(points));
    }

    private void privateNotAnnotated(int x) {
        System.out.println("  privateNotAnnotated: " + x);
    }
}