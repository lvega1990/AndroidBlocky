package com.app.blockydemo.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.content.Script;
import com.app.blockydemo.content.Sprite;

public class JsonConstructor
{
	public JsonConstructor () {
		// do nothing
	}
	
	public JSONObject generateJson () {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		
		int numberOfScripts = sprite.getNumberOfScripts();
		
		JSONObject jsonScripts = new JSONObject();
		
		// at least need to have one script, because the scripts 
		// are the parents of blocks
		for (int scriptPosition = 0; scriptPosition < numberOfScripts; scriptPosition++) {
			Script script = sprite.getScript(scriptPosition);

			try
            {
	            jsonScripts.put(script.getClass().getName(), script.getBrickListJson());
            }
            catch (JSONException e)
            {
	            e.printStackTrace();
            }
		}
		
		return jsonScripts;
	}
}
