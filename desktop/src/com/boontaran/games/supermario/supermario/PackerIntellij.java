package com.boontaran.games.supermario.supermario;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

//this is an utility class to pack the images into a large pack image
public class PackerIntellij {

	public static void main(String[] args) {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.filterMin = TextureFilter.MipMapLinearNearest;
		settings.filterMag = TextureFilter.Linear;
		settings.paddingX = 4;
		settings.paddingY = 4;
		settings.maxHeight = 4096;
		settings.maxWidth = 4096;

		TexturePacker.process(settings, "desktop/raw_images", "android/assets/images", "pack");
	}

}
