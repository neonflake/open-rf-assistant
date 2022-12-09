package hu.open.assistant.rf.graphical.renderer;

import hu.open.assistant.commons.graphical.gui.AssListRenderer;
import hu.open.assistant.rf.model.station.Station;

import javax.swing.JList;
import java.awt.Component;

/**
 * Responsible for displaying a station on a graphical list.
 */
public class StationListRenderer extends AssListRenderer<Station> {

    public StationListRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Station> list, Station station, int index, boolean isSelected, boolean cellHasFocus) {
        String text = station.getSerial() + " (" + Math.round(station.getPercentage()) + "%)";
        if (station.isCompensated()) {
            text = text.concat(" (módosítva)");
        }
        setText(text);
        return super.getListCellRendererComponent(list, station, index, isSelected, cellHasFocus);
    }
}