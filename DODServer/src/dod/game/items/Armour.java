package dod.game.items;

/**
 * A class to represent armour.
 */
public class Armour extends GameItem {
	@Override
	public void processPickUp(GameItemConsumer player) {
		player.equipmentChange("ARMOUR");
	}
	
	@Override
	public boolean isRetainable() {
		// A sword is retained
		return true;
	}

	@Override
	public String toString() {
		return "armour";
	}

	@Override
	public char toChar() {
		return 'A';
	}
}
