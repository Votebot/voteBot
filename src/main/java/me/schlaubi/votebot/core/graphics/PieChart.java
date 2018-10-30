package me.schlaubi.votebot.core.graphics;

import me.schlaubi.votebot.util.Colors;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class PieChart {

    private final String heading;
    private final org.knowm.xchart.PieChart chart;


    public PieChart(String heading, PieTile[] pieTiles) throws IOException, FontFormatException {
        this.heading = heading;
        this.chart = createChart();
        for (PieTile pieTile : pieTiles) {
            chart.addSeries(pieTile.getTile(), pieTile.getPercentage());
        }
        style(chart.getStyler());
    }

    private org.knowm.xchart.PieChart createChart() {
        PieChartBuilder builder = new PieChartBuilder()
                .title(heading)
                .width(600)
                .height(400)
                .theme(Styler.ChartTheme.GGPlot2);
        return builder.build();
    }

    public File encode() throws IOException {
        File outputFile = new File("charts/", ThreadLocalRandom.current().nextInt() + "_chart.jpg");
        outputFile.createNewFile();
        BitmapEncoder.saveBitmap(chart, new FileOutputStream(outputFile), BitmapEncoder.BitmapFormat.PNG);
        return outputFile;
    }


    private void style(Styler styler) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.PLAIN, new File("fonts/", "Product Sans Regular.ttf"));
        styler.setChartBackgroundColor(Colors.FULL_WHITE)
                .setLegendBackgroundColor(Colors.NOT_QUITE_BLACK)
                .setPlotBackgroundColor(Colors.BLURPLE)
                .setAnnotationsFont(font)
                .setLegendFont(font)
                .setToolTipFont(font)
                .setChartFontColor(Color.MAGENTA)
        ;
    }
}
