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

public class Plotter {

    public static void plotResults(int[] xAxis, double[][] yAxis, String[] names, String title, String yAxisLabel, String imagePath) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < yAxis.length; i++) {
            XYSeries series = new XYSeries(names[i]);
            for (int j = 0; j < xAxis.length; j++) {
                series.add(xAxis[j], yAxis[i][j]);
            }
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "log2(N)",
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, new Color(0, 153, 0));
        renderer.setSeriesPaint(3, new Color(255, 128, 0));
        renderer.setBaseShapesVisible(true);
        plot.setRenderer(renderer);

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