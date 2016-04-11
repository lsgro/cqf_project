package com.luigisgro.cqf.fdm;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements a container of other derivatives, exposing a {@link Derivative} interface. Makes it
 * possible to interact with a portfolio as a single instrument
 * @author Luigi Sgro
 *
 */
public class Portfolio implements Derivative {
	/**
	 * Inner class representing a single instrument within the portfolio
	 * @author Luigi Sgro
	 *
	 */
	public class Item {
		private Item() {}
		public Item(double position, double unitPrice, boolean isHedge) {
			this.position = position;
			this.unitPrice = unitPrice;
			this.isHedge = isHedge;
		}
		
		/**
		 * Amount (positive of negative) of the instrument
		 */
		public double position;
		
		/**
		 * Price of 1.0 position of the instrument
		 */
		public double unitPrice;
		
		/**
		 * True if the instrument is used for hedging, false if it must be priced
		 */
		public boolean isHedge;
	}
	
	private Map<Derivative, Item> items = new LinkedHashMap<Derivative, Item>();

	/**
	 * Returns all the derivatives with their attributes, contained in {@link Item} objects
	 * @return A map of {@link Derivative}->{@link Item}
	 */
	public Map<Derivative, Item> getItems() {
		return items;
	}

	@Override
	public double cashflow(double t, double tStep, double s) {
		double cf = 0.0;
		for (Map.Entry<Derivative, Item> item : items.entrySet()) {
			cf += item.getKey().cashflow(t, tStep, s) * item.getValue().position;
		}
		return cf;
	}

	@Override
	public double boundaryValue(double t, double tStep, double s, double r) {
		double bv = 0.0;
		for (Map.Entry<Derivative, Item> item : items.entrySet()) {
			bv += item.getKey().boundaryValue(t, tStep, s, r) * item.getValue().position;
		}
		return bv;
	}

	public void setPosition(Derivative d, double position) {
		if (!items.containsKey(d))
			items.put(d, new Item());
		items.get(d).position = position;
	}
	
	public double getPosition(Derivative d) {
		return items.get(d).position;
	}
	
	public void invertPositions() {
		for (Item item : items.values()) {
			item.position = -item.position;
		}
	}
	
	public Set<Map.Entry<Derivative, Item>> getPositions() {
		return new HashSet<Map.Entry<Derivative, Item>>(items.entrySet());
	}

	@Override
	public double timeToMaturity() {
		double farthestMaturity = 0;
		for (Derivative d : items.keySet()) {
			if (d.timeToMaturity() > farthestMaturity)
				farthestMaturity = d.timeToMaturity();
		}
		return farthestMaturity;
	}

	@Override
	public double timeToNearestCashflow() {
		double nearestMaturity = Double.MAX_VALUE;
		for (Derivative d : items.keySet()) {
			if (d.timeToMaturity() < nearestMaturity)
				nearestMaturity = d.timeToMaturity();
		}
		return nearestMaturity;
	}

	@Override
	public double boundaryValueOpt(int it, int is, Grid g) {
		double bv = 0.0;
		for (Map.Entry<Derivative, Item> pos : items.entrySet()) {
			bv += pos.getKey().boundaryValueOpt(it, is, g) * pos.getValue().position;
		}
		return bv;
	}
}
