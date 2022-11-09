package com.andedit.planet.graphic.vertex;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Null;

public interface VertContext 
{
	ShaderProgram getShader();
	
	VertexAttributes getAttrs();
	
	default void setVertexAttributes() {
		setVertexAttributes(getShader());
	}
	
	default void setVertexAttributes(@Null ShaderProgram shader) {
		shader = shader == null ? getShader() : shader;
		setVertAttrs(shader, getAttrs());
	}
	
	default void unVertexAttributes() {
		unVertexAttributes(getShader());
	}
	
	default void unVertexAttributes(@Null ShaderProgram shader) {
		shader = shader == null ? getShader() : shader;
		unVertAttrs(shader, getAttrs());
	}
	
	static VertContext of(ShaderProgram shader, VertexAttribute... attributeArray) {
		return of(shader, new VertexAttributes(attributeArray));
	}
	
	static VertContext of(ShaderProgram shader, VertexAttributes attributes) {
		return new VertContext() {
			public ShaderProgram getShader() {
				return shader;
			}
			public VertexAttributes getAttrs() {
				return attributes;
			}
		};
	}
	
	static void setVertAttrs(ShaderProgram shader, VertexAttributes attributes) {
		for (int i = 0; i < attributes.size(); i++) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = shader.getAttributeLocation(attribute.alias);
			if (location < 0) continue;
			shader.enableVertexAttribute(location);

			shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
					attributes.vertexSize, attribute.offset);
		}
	}
	
	static void unVertAttrs(ShaderProgram shader, VertexAttributes attributes) {
		for (int i = 0; i < attributes.size(); i++) {
			shader.disableVertexAttribute(attributes.get(i).alias);
		}
	}
}
