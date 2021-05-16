package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;

@Getter
public class Modifier{
	private final int amount;
	private final boolean isFromEffect;
	private final boolean isDisposableEachTurn;
	private GameCard effectCard;

	public Modifier(int amount, boolean isDisposableEachTurn) {
		this.amount = amount;
		this.isFromEffect = false;
		this.isDisposableEachTurn = isDisposableEachTurn;
	}

	public Modifier(int amount, GameCard effectCard, boolean isDisposableEachTurn) {
		this.amount = amount;
		this.isFromEffect = true;
		this.effectCard = effectCard;
		this.isDisposableEachTurn = isDisposableEachTurn;
	}
}