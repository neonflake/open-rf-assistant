package hu.open.assistant.commons.graphical.gui;

import hu.open.assistant.commons.graphical.AssColor;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An extended list GUI component which can be configured on creation and has more functionality. It contains a
 * scroll pane and there are multiple ways you can interact with the list's model which stores the elements in the list.
 *
 * @param <T> class of elements contained in the list
 */
public class AssList<T> extends JList<T> {

    private final AssScrollPane scrollPane;
    private DefaultListModel<T> model;

    /**
     * Create the list.
     *
     * @param name          of the list
     * @param fontSize      of the list elements displayed on the list
     * @param preferredSize width and height in pixels
     * @param listener      responsible for event handling
     * @param renderer      for displaying list elements
     */
    public AssList(String name, int fontSize, Dimension preferredSize, ActionListener listener, ListCellRenderer<T> renderer) {
        setName(name);
        scrollPane = new AssScrollPane(preferredSize, this);
        setFont(new Font("Arial", Font.BOLD, fontSize));
        if (renderer != null) {
            setCellRenderer(renderer);
        }
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBorder(BorderFactory.createLineBorder(AssColor.GENERIC_BORDER_GREY.getColor()));
        setBackground(AssColor.LIST_BACKGROUND_GREY.getColor());
        addListSelectionListener((ListSelectionListener) listener);
        model = new DefaultListModel<>();
        setModel(model);
    }

    /**
     * Enable listening to mouse inputs with the same list selection listener.
     */
    public void enableMouseListening() {
        addMouseListener((MouseListener) this.getListSelectionListeners()[0]);
    }

    /**
     * Enable selection if multiple elements on the list.
     */
    public void enableMultiSelect() {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Return the scroll pane bound to the list.
     */
    public AssScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Remove a specific element from the list.
     *
     * @param element to remove
     */
    public void removeElement(T element) {
        model.removeElement(element);
    }

    /**
     * Add an element to the list.
     *
     * @param element to add
     */
    public void addElement(T element) {
        model.addElement(element);
    }

    /**
     * Add an element to the list to a given position.
     *
     * @param index position of the element
     * @param element to add
     */
    public void addElement(int index, T element) {
        model.add(index, element);
    }

    /**
     * Get an element from the list at a given position.
     *
     * @param index position of the element
     * @return element
     */
    public T getElement(int index) {
        return model.get(index);
    }

    /**
     * Check if list contains the given element.
     *
     * @param element to check for
     * @return true if list contains the element, false otherwise
     */
    public boolean contains(T element) {
        return model.contains(element);
    }

    /**
     * Return how many elements the list contains.
     */
    public int getModelSize() {
        return model.getSize();
    }

    /**
     * Clear the list (remove all elements).
     */
    public void clearModel() {
        model.clear();
    }

    /**
     * Scroll the list to the top position.
     */
    public void scrollToTop() {
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
    }

    /**
     * Change the list's elements to those provided on a list.
     *
     * @param list containing new elements
     * @param reverse place elements in reverse order (last becomes first)
     */
    public void changeModel(List<T> list, boolean reverse) {
        model = new DefaultListModel<>();
        for (T element : list) {
            if (reverse) {
                model.add(0, element);
            } else {
                model.add(model.getSize(), element);
            }
        }
        this.setModel(model);
    }

    /**
     * Return all the list's elements in a list.
     */
    public List<T> getModelAsArrayList() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            list.add(model.get(i));
        }
        return list;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        scrollPane.setVisible(visible);
    }
}
