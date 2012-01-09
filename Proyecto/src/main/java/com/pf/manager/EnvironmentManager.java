package com.pf.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.pf.model.CircleObstacle;
import com.pf.model.Exit;
import com.pf.model.Obstacle;
import com.pf.model.PolygonObstacle;
import com.pf.model.SpawnPoint;

public class EnvironmentManager extends Manager<EnvironmentManager> {

	private float height;
	private float width;
	private float x = 0;
	private float y = 0;
	private Collection<CircleObstacle> circleObstacles;
	private Collection<PolygonObstacle> polygonObstacles;
	private transient List<Obstacle> obstacles;
	private Collection<Exit> exits;
	private Collection<SpawnPoint> spawnPoints;
	public float deltaTime = 0.1f;

	public EnvironmentManager() {

	}

	public EnvironmentManager(String filename) throws Exception {
		initWith(filename);
	}

	public void addObstacle(Obstacle o) {
		obstacles.add(o);
	}

	public void addExit(Exit o) {
		exits.add(o);
	}

	public void addSpawnPoint(SpawnPoint o) {
		spawnPoints.add(o);
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public void setObstacles(List<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	public Collection<Exit> getExits() {
		return exits;
	}

	public void setExits(List<Exit> exits) {
		this.exits = exits;
	}

	public Collection<SpawnPoint> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(List<SpawnPoint> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public void initWith(String filename) throws Exception {
		EnvironmentManager envManager = loadFromFile(filename);
		init(envManager);

	}
	
	public void init(EnvironmentManager envManager)
	{
		this.obstacles = new ArrayList<Obstacle>();

		this.circleObstacles = envManager.circleObstacles;
		this.polygonObstacles = envManager.polygonObstacles;

		if (circleObstacles != null)
			this.obstacles.addAll(envManager.circleObstacles);

		if (polygonObstacles != null)
			this.obstacles.addAll(envManager.polygonObstacles);

		this.spawnPoints = envManager.spawnPoints;
		this.exits = envManager.exits;
		this.height = envManager.height;
		this.width = envManager.width;

		if (this.spawnPoints == null)
			this.spawnPoints = new ArrayList<SpawnPoint>();
		if (this.exits == null)
			this.exits = new ArrayList<Exit>();
		
	}

	@Override
	public void saveToFile(String filename) {
		String environmentJSON = new Gson().toJson(this);

		try {
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(environmentJSON);
			out.close();
		} catch (Exception e) {
			System.err.println("EnvironmentManager [saveToFile] : "
					+ e.getMessage());
		}
	}

	public static EnvironmentManager loadFromFile(String filename) throws Exception {
		StringBuilder envJSON = new StringBuilder();
		String read;

		try {
			FileReader fstream;
			fstream = new FileReader(filename);
			BufferedReader in = new BufferedReader(fstream);
			while ((read = in.readLine()) != null) {
				envJSON.append(read);
			}
			EnvironmentManager envManager = new Gson().fromJson(envJSON
					.toString(), EnvironmentManager.class);
			if (envManager == null || 
					envManager.getHeight()== 0 || 
					envManager.getWidth() == 0)
				throw new Exception("Invalid Environment File");
			return envManager;
		} catch (Exception e) {
			System.err
					.println("Environment [LoadFromFile] : " + e.getMessage());
			throw new Exception(e);
		}


		
	}

	public Collection<CircleObstacle> getCircleObstacles() {
		return circleObstacles;
	}

	public void setCircleObstacles(Collection<CircleObstacle> circleObstacles) {
		this.circleObstacles = circleObstacles;
	}

	public Collection<PolygonObstacle> getPolygonObstacles() {
		return polygonObstacles;
	}

	public void setPolygonObstacles(Collection<PolygonObstacle> polygonObstacles) {
		this.polygonObstacles = polygonObstacles;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}
}
