package faceduck.actors;

import com.sun.xml.internal.ws.api.config.management.Reconfigurable;
import faceduck.custom.Actionable;
import faceduck.custom.util.Actors;
import faceduck.custom.util.Recognizable;
import faceduck.skeleton.interfaces.*;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class FoxImpl extends Actionable implements Fox {
	private static final int FOX_MAX_ENERGY = 160;
	private static final int FOX_VIEW_RANGE = 5;
	private static final int FOX_BREED_LIMIT = FOX_MAX_ENERGY * 3 / 4;
	private static final int FOX_COOL_DOWN = 2;
	private static final int FOX_INITIAL_ENERGY = FOX_MAX_ENERGY * 1 / 2;

	public FoxImpl() {
		super();
		addEdible(RabbitImpl.class);
	}

	@Override
	public int getInitialEnergy() {
		return FOX_INITIAL_ENERGY;
	}

	@Override
	public int getMaxEnergy() {
		return FOX_MAX_ENERGY;
	}

	@Override
	public int getBreedLimit() {
		return FOX_BREED_LIMIT;
	}

	@Override
	public int getViewRange() {
		return FOX_VIEW_RANGE;
	}

	@Override
	public int getCoolDown() {
		return FOX_COOL_DOWN;
	}

	@Override
	protected double judge(Actors actor) {
		switch (actor) {
			case GRASS:
				return Recognizable.IRRELEVANT.getValue();
			case GNAT:
				return Recognizable.IRRELEVANT.getValue();
			case RABBIT:
				return Recognizable.EDIBLE.getValue();
			case FOX:
				return Recognizable.IRRELEVANT.getValue();
			case GARDENER:
				return Recognizable.IRRELEVANT.getValue();
		}
        return Recognizable.EMPTY.getValue();
	}
}
