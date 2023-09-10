package rs.ac.bg.etf.cuckoo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Plot {

    public static void plotResults(int[] xAxis, double[][] yAxis, String[] names) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < yAxis.length; i++) {
            XYSeries series = new XYSeries(names[i]);
            for (int j = 0; j < xAxis.length; j++) {
                series.add(xAxis[j], yAxis[i][j]);
            }
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Cuckoo Hashing Variants Runtime",
                "log2(N)",
                "runtime [ms]",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        // Customize series colors and shapes
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesPaint(3, Color.YELLOW);
        plot.setRenderer(renderer);

        // Define the file path for saving the chart as an image
        String imagePath = "cuckoo_hashing.png";

        // Save the chart as an image (PNG format)
        int width = 800;
        int height = 600;
        File imageFile = new File(imagePath);
        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
            System.out.println("Chart saved as " + imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}