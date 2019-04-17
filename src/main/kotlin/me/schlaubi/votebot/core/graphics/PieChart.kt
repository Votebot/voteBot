/*
 * VoteBot - A unique Discord bot for surveys
 *
 * Copyright (C) 2019  Michael Rittmeister
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package me.schlaubi.votebot.core.graphics

import cc.hawkbot.regnum.client.util.Colors
import me.schlaubi.votebot.util.Utils
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.PieChart
import org.knowm.xchart.PieChartBuilder
import org.knowm.xchart.style.PieStyler
import org.knowm.xchart.style.Styler
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO

/**
 * Pie chart used for summarizing vote result
 * @param heading the title of the vote
 * @param tiles an array of all vote options
 */
class PieChart(
    private val heading: String,
    tiles: Array<PieTile>
) {

    private val chart: PieChart
    private val font = Font.createFont(Font.TRUETYPE_FONT, FileInputStream("fonts/Product Sans Regular.ttf"))
    private val logo = ImageIO.read(File("logos", "logo.png"))
    private lateinit var bufferedImage: BufferedImage

    init {
        chart = createChart()
        // Add all options and their percentage to chart
        tiles.forEach {
            chart.addSeries(it.title, it.percentage)
        }
        // Apply VoteBot's super cool stlye
        style(chart.styler)
    }

    private fun createChart(): PieChart {
        return PieChartBuilder()
            .title(if (heading.length >= 15) heading.substring(0, 15) + "..." else heading)
            .width(600)
            .height(400)
            .theme(Styler.ChartTheme.GGPlot2)
            .build()
    }

    private fun style(styler: PieStyler) {
        styler.chartFontColor = Colors.NOT_QUITE_BLACK
        styler.chartTitleFont = font.deriveFont(Font.BOLD, 25F)
        styler.chartBackgroundColor = Colors.DARK_BUT_NOT_BLACK
        styler.legendFont = font.deriveFont(Font.PLAIN, 20F)
        styler.legendBackgroundColor = Colors.FULL_WHITE
        styler.annotationsFont = font.deriveFont(Font.PLAIN, 15F)
        styler.plotBackgroundColor = Colors.FULL_WHITE
        styler.seriesColors = Utils.SERIES_COLORS
        styler.chartTitleBoxBackgroundColor = Colors.GREYPLE
        this.bufferedImage = encode()
        val graphics = bufferedImage.createGraphics()
        graphics.font = font.deriveFont(Font.PLAIN, 15F)
        graphics.drawString("Powered by", 490, 380);
        graphics.drawImage(logo, 570, 360, 25, 25, null);
    }

    private fun encode() = BitmapEncoder.getBufferedImage(chart)

    fun toInputStream(): InputStream {
        // Create output stream
        val bos = ByteArrayOutputStream()
        // Write image to output stream
        ImageIO.write(bufferedImage, "png", bos)
        // Create input stream from outputstream
        return ByteArrayInputStream(bos.toByteArray())
    }
}