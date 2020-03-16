package cz.cuni.mff.agents.arnold;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;
import ch.idsia.benchmark.mario.engine.generalization.Entity;
import ch.idsia.benchmark.mario.engine.generalization.EntityType;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.MarioLog;
import cz.cuni.mff.LevelConfig;

/**
 * Code your custom agent here!
 * <p>
 * Change {@link #actionSelection()} implementation to alter the behavior of your Mario.
 * <p>
 * Change {@link #debugDraw(VisualizationComponent, LevelScene, IEnvironment, Graphics)} to draw custom debug stuff.
 * <p>
 * You can change the type of level you want to play in {@link #main(String[])}.
 * <p>
 * Once you have your agent ready, you may use {@link Evaluate} class to benchmark the quality of your AI.
 */
public class ArnoldRuleBasedAgent extends MarioHijackAIBase implements IAgent {

	public class Coord {
		public int x;
		public int y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public void reset(AgentOptions options) {
		super.reset(options);
	}

	@Override
	public void debugDraw(VisualizationComponent vis, LevelScene level, IEnvironment env, Graphics g) {
		super.debugDraw(vis, level, env, g);
		if (mario == null) return;

		// provide custom visualization using 'g'

		// EXAMPLE DEBUG VISUALIZATION
		String debug = "MY DEBUG STRING";
		VisualizationComponent.drawStringDropShadow(g, Float.toString(Math.round(mario.speed.x)) + " " + Float.toString(Math.round(mario.speed.y)), 0, 26, 1);
	}

	public List<Coord> coordRect(int x, int y) {
		int negateX = x < 0 ? -1 : 1;
		x = Math.abs(x);

		int negateY = y < 0 ? -1 : 1;
		y = Math.abs(y);

		ArrayList<Coord> coords = new ArrayList<>(Math.abs(x * y));

		for (int i = 0; i <= x; i++) {
			for (int j = 0; j <= y; j++) {
				coords.add(new Coord(negateX * i, negateY * j));
			}
		}

		return coords;
	}

	public boolean dangerInRect(int x, int y) {
		boolean isDanger = false;

		for (Coord coord : coordRect(x, y)) {
			isDanger = isDanger || (e.danger(coord.x, coord.y) && t.emptyTile(coord.x, coord.y));
		}

		return isDanger;
	}

	public boolean escapeLeft() {
		if (dangerInRect(-1, 0)) {
			control.runLeft();
			return true;
		} else {
			return false;
		}
	}

	boolean dangerInRectNotLevel(int x, int y) {
		boolean danger = false;

		for (Coord coord : coordRect(4, -4)) {
			if (coord.y == 0) continue;

			if (e.danger(coord.x, coord.y)) {
				danger = true;
			}
		}

		return danger;
	}

	List<Entity> entitiesInRect(int x, int y) {
		List<Entity> result = new ArrayList<>();

		for (Coord coord : coordRect(x, y)) {
			for (Entity entity : e.entities(coord.x, coord.y)) {
				if (entity.type == EntityType.DANGER) {
					result.add(entity);
				}
			}
		}

		return result;
	}

	void jumpWallRight() {
		if (mario.onGround && (t.emptyTile(1, 1) || t.emptyTile(1, 2))) {
			control.jump();
			control.runRight();
		}
	}

	void jumpWallLeft() {
		if (mario.onGround && (t.emptyTile(-1, 1) || t.emptyTile(-1, 2))) {
			control.jump();
			control.runLeft();
		}
	}

	void fallOppositeOfEnemy() {
		List<Entity> enemies = entitiesInRect(0, 6);

		if (!enemies.isEmpty()) {
			Entity enemy = enemies.get(0);

			if (enemy.speed.x > 0) {
				control.runRight();
			} else {
				control.runLeft();
			}

			control.sprint();
		}
	}

	void keepMoving() {
		if (mario.speed.x >= 0) {
			control.runRight();
		} else {
			control.runLeft();
		}
	}


	public void safeMoveRight() {
		boolean dangerStraightRight = dangerInRect(1, 0);

		if (mario.onGround) {
			if (dangerStraightRight) {
				control.jump();
			} else {
				control.runRight();
			}
		} else {
			control.runRight();
			control.jump();
		}
	}

	public void safeMoveLeft() {
		boolean dangerStraightRight = dangerInRect(-1, 0);

		control.sprint();
		control.runLeft();

		if (mario.onGround) {
			if (dangerStraightRight) {
				control.jump();
			}
		} else {
			control.jump();
		}
	}

	int count = 0;

	void print(String message) {
//		System.out.println(count++ + " " + message);
	}

	@Override
	public MarioInput actionSelectionAI() {
		// ALWAYS RUN RIGHT
//        control.sprint();

		if (dangerInRect(2, 2)) {
			control.shoot();
		}

		boolean movingUp = mario.speed.y < 0;
		boolean isFalling = !mario.onGround && mario.speed.y > 0;

		boolean dangerAbove = dangerInRect(4, -6) && !dangerInRect(4, 0);
		boolean dangerBelow = dangerInRect(3, 6) && !dangerInRect(3, 0);

		boolean roadAhead = !t.brick(1, 0) && t.brick(1, 1);
		boolean holeAhead = t.emptyTile(1, 1);

		boolean wallAhead = t.brick(1, 0) || t.brick(2, 0);

		if (mario.onGround) {
			if (wallAhead) {

				if (dangerInRect(3, -4)) {
					boolean jetamkytka = false;
					for (Entity entity : entitiesInRect(3, -4)) {
						if (entity.type == EntityType.ENEMY_FLOWER) {
							jetamkytka = true;
							break;
						}
					}

					if (jetamkytka) {
						print("skacu pres kytku");
						control.runRight();
						control.jump();
					} else {
						print("uhejbam nekytce");
						safeMoveLeft();
					}

				} else {
					print("kopec ahead, skacu na kopec");
					control.runRight();
					control.jump();
				}
			} else if (holeAhead) {

				if (dangerInRect(1, -6)) {
					print("hole ahead, cekam");
					// cekam na kraji propasti
				} else {
					print("dira ale necekam");
					control.runRight();
					control.jump();
				}
			} else if (roadAhead) {
				print("road ahead");
				safeMoveRight();
			}
		} else {

			if (isFalling) {
				if (dangerInRect(0, 8)) {
					print("Vyhejbam se, abych nespad na nepritele");

					fallOppositeOfEnemy();
				} else {
					if (dangerInRect(2, 3)) {
						if (!dangerInRect(1, 3)) {
							print("brzdim");
							// TODO: ale ne na nej
							// padam primo dolu
							if (mario.speed.x > 0) {
								control.runLeft();
								control.sprint();
							} else if (mario.speed.x < 0) {
								control.runRight();
								control.sprint();
							}
						} else {
							print("nepritel! pryc doleva");
							control.runLeft();
							control.sprint();
						}

					} else {
						print("nikde nic, jdeme dal");
						keepMoving();
					}
				}
			} else if (movingUp) {
				// nepritel nademnou na balkone
				if (dangerInRect(3, -3) && !t.brick(0, -1)) {
					print("uhejbam nepriteli na balkonu");
					control.runLeft();
					control.sprint();
					control.jump();
				} else {
					if (dangerInRect(2, 7)) {
						print("nepritel podemnou");
						control.sprint();
						control.runRight();
						control.jump();
					} else{
						print("skacu a du doprava");
						control.runRight();
						control.jump();
					}
				}
			}
		}




		// RETURN THE RESULT
		return action;
	}

	private boolean enemyAhead() {
		return
				e.danger(1, 0) || e.danger(1, -1)
						|| e.danger(2, 0) || e.danger(2, -1)
						|| e.danger(3, 0) || e.danger(2, -1);
	}

	public static void main(String[] args) {
		// YOU MAY RISE LEVEL OF LOGGING, even though there are probably no inforamation you need to know...
		//MarioLog.setLogLevel(Level.ALL);

		// UNCOMMENT THE LINE OF THE LEVEL YOU WISH TO RUN

//		LevelConfig level = LevelConfig.LEVEL_0_FLAT;
//		LevelConfig level = LevelConfig.LEVEL_1_JUMPING;
//		LevelConfig level = LevelConfig.LEVEL_2_GOOMBAS;
//        LevelConfig level = LevelConfig.LEVEL_3_TUBES;
		LevelConfig level = LevelConfig.LEVEL_4_SPIKIES;

		// CREATE SIMULATOR
		MarioSimulator simulator = new MarioSimulator(level.getOptions());

		// CREATE SIMULATOR AND RANDOMIZE LEVEL GENERATION
		// -- if you wish to use this, comment out the line above and uncomment line below
		//MarioSimulator simulator = new MarioSimulator(level.getOptionsRandomized());

		// INSTANTIATE YOUR AGENT
		IAgent agent = new ArnoldRuleBasedAgent();

		// RUN THE SIMULATION
		EvaluationInfo info = simulator.run(agent);

		// CHECK RESULT
		switch (info.getResult()) {
			case LEVEL_TIMEDOUT:
				MarioLog.warn("LEVEL TIMED OUT!");
				break;

			case MARIO_DIED:
				MarioLog.warn("MARIO KILLED");
				break;

			case SIMULATION_RUNNING:
				MarioLog.error("SIMULATION STILL RUNNING?");
				throw new RuntimeException("Invalid evaluation info state, simulation should not be running.");

			case VICTORY:
				MarioLog.warn("VICTORY!!!");
				break;
		}
	}

}