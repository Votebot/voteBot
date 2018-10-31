package me.schlaubi.votebot.core.graphics;

import me.schlaubi.votebot.util.Colors;
import me.schlaubi.votebot.util.Misc;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.Styler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class PieChart {

    private final String heading;
    private final org.knowm.xchart.PieChart chart;
    private BufferedImage bufferedImage;


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
                .title(heading.length() >= 15 ? heading.substring(0, 15) + "..." : heading)
                .width(600)
                .height(400)
                .theme(Styler.ChartTheme.GGPlot2);
        return builder.build();
    }
    private BufferedImage encode() {
        return BitmapEncoder.getBufferedImage(chart);
    }


    private void style(Styler styler) throws IOException, FontFormatException {
        var font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("./fonts/Product Sans Regular.ttf"));
        var colorsList = Arrays.asList(Misc.SERIES_COLORS);
        //randomize
        Collections.shuffle(colorsList);

        styler.setChartFontColor(Colors.NOT_QUITE_BLACK)
                .setChartTitleFont(font.deriveFont(Font.BOLD, 25))
                .setLegendFont(font.deriveFont(Font.PLAIN, 20))
                .setAnnotationsFont(font.deriveFont(Font.PLAIN, 15))
                .setLegendBackgroundColor(Colors.FULL_WHITE)
                .setChartBackgroundColor(Colors.DARK_BUT_NOT_BLACK)
                .setPlotBackgroundColor(Colors.FULL_WHITE)
                .setSeriesColors(colorsList.toArray(new Color[0]))
                .setChartTitleBoxBackgroundColor(Colors.GREYPLE);
        this.bufferedImage = encode();
        var graphics = bufferedImage.createGraphics();
        graphics.setFont(font.deriveFont(Font.PLAIN, 15));
        graphics.drawString("Powered by", 490, 380);
        Image logo = ImageIO.read(new File("logo", "logo.png"));
        graphics.drawImage(logo, 570, 360, 25, 25, null);
    }

    public InputStream toInputStream() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,"png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
