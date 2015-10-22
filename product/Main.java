package product;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Main {

    private static Map<String,Integer> colors;
    private static Map<String,Average> products;

    public static void main(String[] args) {
        String folderpath = args[0];
        String filepath = args[1];
        colors = new TreeMap<String, Integer>();
        products = new TreeMap<String, Average>();

        processColors(filepath);
        System.out.println("Processed colors:");

        colors.keySet().forEach(System.out::println);
       	Path productsDirectory = Paths.get(folderpath);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(productsDirectory, "*.txt")) {
				stream.forEach(file -> {
					processProducts(file);
					System.out.println(file);
				});
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println("Processed products:");
        products.keySet().stream().forEach(System.out::println);


        generateResult(folderpath + "\\result.txt");
    }

    public static void processColors(String filepath){
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
        	while (br.ready()) {
	        	String line = br.readLine();
	        	String parts[] = line.split(" ");
	        	//matches("\\d+") means that string contains only one or more digits
	        	if (parts.length != 2 || !parts[1].matches("\\d+")) continue;
	        	String name = parts[0];
	        	int price = Integer.valueOf(parts[1]);
                if (colors.get(name) == null) colors.put(name, price);

        	}
        } catch (Exception e){
            System.err.println("Exception while trying to read file with colors data " + e);
        }
    }

    public static void processProducts(Path filepath){
    	
    	 try (BufferedReader br = Files.newBufferedReader(filepath)) {
    		Set<String> productsInCurrentFile = new HashSet<>();
         	while (br.ready()) {
 	        	String line = br.readLine();
 	        	String parts[] = line.split(" ");
 	        	if (parts.length != 2 || !parts[1].matches("\\d+")) continue;
 	        	String name = parts[0];
 	        	int price = Integer.valueOf(parts[1]);
 	        	if (productsInCurrentFile.contains(name)) continue;
 	        	Average previousAverage = products.get(name);
                if (previousAverage == null) {
                	products.put(name, new Average(price));
                } else {
                	
                	Average actualAverage = new Average(price);                	
                	actualAverage.add(previousAverage);
                	products.put(name, actualAverage);
                }
                productsInCurrentFile.add(name);
         	}
         } catch (Exception e){
             System.err.println("Exception while trying to read file with colors data " + e);
         }
    }

    public static void generateResult(String filepath){
        try (BufferedWriter f = new BufferedWriter(new FileWriter(filepath));) {
        	if (products.size() == 0) {
        		f.write("Products files have incorrect format or empty");
        		return;
        	}
        	if (colors.size() == 0) {
        		products.entrySet().forEach( 
                		product -> {
        					try {
            					f.write(product.getKey());
								f.write(" " + product.getValue().getAverage());
            					f.newLine();
							} catch (Exception e) {
								e.printStackTrace();
							}
                		});
				return;
        	}
        	
        	
            products.entrySet().forEach( 
            		product -> colors.entrySet().forEach(
            				color -> {
            					try {
                					f.write(product.getKey() + "_" + color.getKey());
									f.write(" " + (product.getValue().getAverage() + color.getValue()));
	            					f.newLine();
								} catch (Exception e) {
									e.printStackTrace();
								}
            				}));
         
        } catch(IOException e) {
            System.err.println("Unable to write results into " + filepath);
        }
    }

 


}
