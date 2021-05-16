package edu.sharif.ce.apyugioh.model;

import lombok.Getter;

@Getter
public class Modifier{
	private final int amount;
	private final boolean isFromEffect;
	private final boolean isDisposableEachTurn;
	
	public Modifier(int amount, boolean isFromEffect, boolean isDisposableEachTurn) {
		this.amount = amount;
		this.isFromEffect = isFromEffect;
		this.isDisposableEachTurn = isDisposableEachTurn;
	}
}