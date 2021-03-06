package org.terasology.codecity.world.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.terasology.codecity.world.loader.CodeCityLoader;
import org.terasology.codecity.world.map.CodeMap;
import org.terasology.codecity.world.map.CodeMapFactory;
import org.terasology.codecity.world.map.DrawableCode;
import org.terasology.codecity.world.map.DrawableCodeFactory;
import org.terasology.codecity.world.structure.CodeRepresentation;
import org.terasology.codecity.world.structure.metric.CodeMetricManager;
import org.terasology.codecity.world.structure.scale.CodeScaleManager;
import org.terasology.engine.SimpleUri;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;

/**
 * Generate a new world using information provided by JEdit
 */
@RegisterWorldGenerator(id = "codecity", displayName = "CodeCity",
		description = "Generates the world using a CodeCity structure")
public class CodeCityWorldGenerator extends BaseFacetedWorldGenerator {
	private String path = "";

	public CodeCityWorldGenerator(SimpleUri uri) {
		super(uri);
	}

	@Override
	public void initialize(String s) {
		CoreRegistry.put(CodeScaleManager.class, new CodeScaleManager());
		CoreRegistry.put(CodeMetricManager.class, new CodeMetricManager());
		this.path = s;
		CodeCityLoader loader;
		if (this.path != "") {
			loader = new CodeCityProjectLoader(this.path);
		} else {
			//this.path = "/home/adderou/Projects/MyPacman/";
			this.path = System.getProperty("user.dir") + File.separator + "modules" + File.separator + "WorldCodecity";
			loader = new CodeCityProjectLoader(this.path);
		}
		CodeRepresentation code = loader.loadCodeRepresentation();
		CodeMap codeMap = generateCodeMap(code);
		CoreRegistry.put(CodeMap.class, codeMap);
		CoreRegistry.put(CodeRepresentation.class, code);
		super.initialize(s);
	}

	@Override
	protected WorldBuilder createWorld(long seed) {
		return new WorldBuilder(seed).addProvider(new CodeCityGroundProvider())
				.addProvider(new CodeCityBuildingProvider()).addRasterizer(new CodeCityGroundRasterizer())
				.addRasterizer(new CodeCityBuildingRasterizer()).setSeaLevel(0);
	}

	/**
	 * Insert into the CodeRegistry the DrawableCode, gen
	 * 
	 * @param code
	 */
	public static CodeMap generateCodeMap(CodeRepresentation code) {
		DrawableCodeFactory drawableFactory = new DrawableCodeFactory();
		List<DrawableCode> list = new ArrayList<DrawableCode>();
		list.add(drawableFactory.generateDrawableCode(code));
		CodeMapFactory factory = new CodeMapFactory();
		return factory.generateMap(list);
	}

}
