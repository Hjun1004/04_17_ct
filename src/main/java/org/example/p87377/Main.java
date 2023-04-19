package org.example.p87377;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    // 나오는 식 (x,y)에 하나씩 넣어보면서 0이 되는 값들 다 찍기
}

class Solution {
    public String[] solution(int[][] line) {
        //Set<Point> points = intersections(line);
        Points points = intersections(line);
        // 매트릭스로 옮긴다.
        char[][] matrix = points.transformToMatrix();

        return drawOnCoordinate(matrix);
    }

    public Point intersection(int[] line1, int[] line2) {

        double A = line1[0];
        double B = line1[1];
        double E = line1[2];

        double C = line2[0];
        double D = line2[1];
        double F = line2[2];

        double divisor;

        // 아래와 같은 경우 평행에서 교점이 없다.
        if((divisor = A*D-B*C) == 0 ) return null;

        // 문제에서 정수좌표만 구하라고 했다.
        double x = ( B * F - E * D ) / divisor;
        // 문제에서 정수좌표만 구하라고 했다.
        double y = ( E * C - A * F ) / divisor;

        if(x != (long) x) return null;
        if(y != (long) y) return null;

        return Point.of((long) x,(long) y);
    }


    public Points intersections(int[][] line) {
        Points points = Points.of(); // 비어있는 HashSet이 생성되어 points객체에 저장된다.
        for(int i = 0 ; i < line.length ; i++){
            for(int j = i+1 ; j < line.length ; j++){
                int[] line1 = line[i];
                int[] line2 = line[j];

                Point point = intersection(line1,line2);

                if (point != null) points.add(point);
            }
        }
        return points;
    }


    public String[] drawOnCoordinate(char[][] matrix){
        return Ut.revRange(0, matrix.length)
                .boxed()
                .map(i->matrix[i])
                .map(row -> new String(row))
                .toArray(String[]::new);
    }
}

class Point{
    public final long x;

    public final long y;

    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public static Point of(long x, long y){
        return new Point(x,y);
    }

    public static Point of(double x, double y){
        return of((long) x,(long) y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


}

class Points implements Iterable<Point>{
    private final Set<Point> data;

    private Points(Set<Point> data){
        this.data = data;
    }

    //Point... == Point[] 와 같은 뜻
    //Point... 의 특수기능: 가변인자
    // Points.of(arg1);
    // Points.of(arg1, arg2);
    // Points.of(arg1, arg2, arg3);
    public static Points of(Point... pointArray){
        // 입력받은 배열을 HashSet형태로 한다.
        // Collectors.toSet() 를 사용하지 않는 이유 : 우리는 mutable한 것을 원한다.
        // mutable
        return new Points(
                Arrays.stream(pointArray)
                        .collect(Collectors.toCollection(HashSet::new))
        );

    }

    public boolean add(Point point){
        return data.add(point);
    }

    public Set<Point> toSet(){
        return data;
    }

    public Point getMinPoint() {
        long x = Long.MAX_VALUE;
        long y = Long.MAX_VALUE;

        for(Point point : data){ // 이런식으로 반복문돌기 위해서는 배열이나 List가 와야하는데 우리가 만든 클래스가 왔기 때문에 Iterator활용한다.
            x = Math.min(x, point.x);
            y = Math.min(y, point.y);
        }

        return Point.of(x,y);
    }

    public Point getMaxPoint() {
        long x = Long.MIN_VALUE;
        long y = Long.MIN_VALUE;

        for(Point point : data){
            x = Math.max(x, point.x);
            y = Math.max(y, point.y);
        }

        return Point.of(x,y);
    }

    public char[][] emptyMatrix(){
        Point minPoint = getMinPoint();
        Point maxPoint = getMaxPoint();

        int width = (int)(maxPoint.x - minPoint.x + 1);
        int height = (int)(maxPoint.y - minPoint.y + 1);

        char[][] matrix = new char[height][width];

        Arrays.stream(matrix).forEach(row -> Arrays.fill(row,'.'));
        return matrix;
    }


    public char[][] transformToMatrix() {
        char[][] matrix = emptyMatrix();
        Points point = positivePoints();

        point.forEach(p -> matrix[(int)p.y][(int)p.x] = '*');
        return matrix;
    }

    public Points positivePoints() {
        Point minPoint = getMinPoint();

        // 원래라면 Set<Point> points의 객체라서 가능한데
        // 지금은 우리가만든 Points클래스의 객체이기 때문에 Stream() 메서드를 생성해서 Set<Point> data를 return받아온다.
        return Points.of(
                data.stream()
                        .map(p -> Point.of(p.x-minPoint.x, p.y- minPoint.y))
                        .toArray(Point[]::new) // point배열로 하기위해서 꼭 해야한다.
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Points points)) return false;

        return Objects.equals(data, points.data);
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public Iterator<Point> iterator() {
        return data.iterator();
    }

    public Stream<Point> stream() {
        return data.stream();
    }

}

class Ut{
    static IntStream revRange(int from, int to){
        return IntStream.range(from,to).map(i-> to - i + from -1);
    }
}
