import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PackageDeliveryOptimizationVisual extends JPanel {

    private List<Point> destinations;
    private List<Integer> bestRoute;
    private List<List<Integer>> population;
    private double[][] distanceMatrix;
    private int currentGeneration;

    public PackageDeliveryOptimizationVisual(List<Point> destinations, double[][] distanceMatrix, int populationSize) {
        this.destinations = destinations;
        this.distanceMatrix = distanceMatrix;
        this.currentGeneration = 0;

        setPreferredSize(new Dimension(800, 800)); // Adjust panel size for better visualization
        setBackground(Color.WHITE);

        // Initialize the population
        this.population = initializePopulation(populationSize, destinations.size());
        this.bestRoute = getBestRoute(population);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw destinations with numbers
        g.setColor(Color.BLUE);
        Font font = new Font("Arial", Font.PLAIN, 12);
        g.setFont(font);
        for (int i = 0; i < destinations.size(); i++) {
            Point destination = destinations.get(i);
            g.fillOval(destination.x - 5, destination.y - 5, 10, 10);
            g.drawString(Integer.toString(i), destination.x + 10, destination.y + 5); // Display destination number
        }

        // Draw current best route
        if (bestRoute != null) {
            g.setColor(Color.RED);
            for (int i = 0; i < bestRoute.size() - 1; i++) {
                Point from = destinations.get(bestRoute.get(i));
                Point to = destinations.get(bestRoute.get(i + 1));
                g.drawLine(from.x, from.y, to.x, to.y);
            }
        }

        // Display current generation and best route distance
        g.setColor(Color.BLACK);
        g.drawString("Generation: " + currentGeneration, 20, 20);
        if (bestRoute != null) {
            double bestDistance = calculateTotalDistance(bestRoute, distanceMatrix);
            g.drawString("Best Route Distance: " + String.format("%.2f", bestDistance), 20, 40);
        }
    }

    public void runEvolution(int numGenerations) {
        // Perform evolutionary algorithm for specified number of generations
        for (int gen = 1; gen <= numGenerations; gen++) {
            // Select parents and create new population
            List<List<Integer>> newPopulation = new ArrayList<>();
            for (int i = 0; i < population.size(); i += 2) {
                List<Integer> parent1 = population.get(i);
                List<Integer> parent2 = population.get(i + 1);

                // Apply crossover and mutation to create offspring
                List<Integer> child1 = crossover(parent1, parent2);
                List<Integer> child2 = crossover(parent2, parent1);

                child1 = mutate(child1);
                child2 = mutate(child2);

                newPopulation.add(child1);
                newPopulation.add(child2);
            }

            // Update population and best route
            population = newPopulation;
            bestRoute = getBestRoute(population);
            currentGeneration = gen;

            // Repaint the panel to update the visualization
            repaint();

            // Pause for visualization
            try {
                Thread.sleep(500); // Adjust delay (in milliseconds) as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> crossover(List<Integer> parent1, List<Integer> parent2) {
        // Implement ordered crossover
        Random random = new Random();
        int crossoverPoint = random.nextInt(parent1.size() - 1) + 1;
        Set<Integer> parent1Subset = new HashSet<>(parent1.subList(0, crossoverPoint));
        List<Integer> child = new ArrayList<>(parent1Subset);
        for (int gene : parent2) {
            if (!parent1Subset.contains(gene)) {
                child.add(gene);
            }
        }
        return child;
    }

    private List<Integer> mutate(List<Integer> route) {
        // Implement swap mutation
        Random random = new Random();
        int pos1 = random.nextInt(route.size());
        int pos2 = random.nextInt(route.size());
        Collections.swap(route, pos1, pos2);
        return route;
    }

    private List<List<Integer>> initializePopulation(int populationSize, int numLocations) {
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Integer> route = new ArrayList<>();
            for (int j = 0; j < numLocations; j++) {
                route.add(j);
            }
            Collections.shuffle(route);
            population.add(route);
        }
        return population;
    }

    private List<Integer> getBestRoute(List<List<Integer>> population) {
        double minDistance = Double.MAX_VALUE;
        List<Integer> bestRoute = null;
        for (List<Integer> route : population) {
            double distance = calculateTotalDistance(route, distanceMatrix);
            if (distance < minDistance) {
                minDistance = distance;
                bestRoute = route;
            }
        }
        return bestRoute;
    }

    private double calculateTotalDistance(List<Integer> route, double[][] distanceMatrix) {
        double totalDistance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            int from = route.get(i);
            int to = route.get(i + 1);
            totalDistance += distanceMatrix[from][to];
        }
        return totalDistance;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Package Delivery Optimization Program!");

        System.out.print("Enter the number of destinations: ");
        int numDestinations = scanner.nextInt();

        List<Point> destinations = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numDestinations; i++) {
            int x = random.nextInt(500) + 50;
            int y = random.nextInt(500) + 50;
            destinations.add(new Point(x, y));
        }

        double[][] distanceMatrix = new double[numDestinations][numDestinations];
        System.out.println("Enter the distance between each pair of destinations in kilometers (enter '0' for same destination):");
        for (int i = 0; i < numDestinations; i++) {
            for (int j = 0; j < numDestinations; j++) {
                if (i != j) {
                    System.out.print("Distance from destination " + i + " to " + j + ": ");
                    distanceMatrix[i][j] = scanner.nextDouble();
                }
            }
        }

        int populationSize;
        do {
            System.out.print("Enter the population size (must be even and >= 4): ");
            populationSize = scanner.nextInt();
        } while (populationSize < 4 || populationSize % 2 != 0);

        System.out.print("Enter the number of generations: ");
        int numGenerations = scanner.nextInt();

        // Create the visualization panel
        PackageDeliveryOptimizationVisual panel = new PackageDeliveryOptimizationVisual(destinations, distanceMatrix, populationSize);

        // Display GUI with visualization
        JFrame frame = new JFrame("Package Delivery Optimization Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Run the evolutionary algorithm and update the visualization
        panel.runEvolution(numGenerations);

        scanner.close();
    }
}
