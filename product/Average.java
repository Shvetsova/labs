package product;

public class Average {
    public int count;
    public int value;

    public Average(int value){
        this.value = value;
        this.count = 1;
    }

    public void add(Average av){
        this.value += av.value;
        this.count += av.count;
    }
    
    
    public double getAverage(){
    	return value / count;
    }
}
