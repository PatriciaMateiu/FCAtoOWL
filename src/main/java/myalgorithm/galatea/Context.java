/*
   Copyright 2009 Jean-Rémy Falleri

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package myalgorithm.galatea;

import myalgorithm.galatea.composite.Facet;

import java.util.*;

public class Context {
	
	protected Set<Attribute> attributes;
	
	protected Set<Entity> entities;
	
	protected Map<Entity, Set<Attribute>> relation;
	
	protected Map<Attribute, Set<Entity>> reverseRelation;
	
	public Context() {
		this.attributes = new LinkedHashSet<Attribute>();
		this.entities = new LinkedHashSet<Entity>();
		this.relation = new LinkedHashMap<Entity, Set<Attribute>>();
		this.reverseRelation = new LinkedHashMap<Attribute, Set<Entity>>();
	}
	
	public void addPair(Entity e,Attribute a) {
		if ( !relation.containsKey(e) )
			relation.put(e,new HashSet<Attribute>());
		
		if ( !reverseRelation.containsKey(a) )
			reverseRelation.put(a,new HashSet<Entity>());
		
		relation.get(e).add(a);
		reverseRelation.get(a).add(e);
	}

	
	public boolean hasPair(Entity e,Attribute a) {
		return this.getAttributes(e).contains(a);
	}

	
	public int getPairNb() {
		int p = 0;
		for( Set<Attribute> attrs: relation.values() ) p += attrs.size();
		return p;
	}
	
	public void addAttribute(Attribute a) {
		this.attributes.add(a);
	}
	
	public Set<Attribute> getAttributes(Entity e) {
		if ( relation.containsKey(e) )
			return relation.get(e);
		else
			return new HashSet<Attribute>();
	}

	//added by me
	public void removeEmpties(){
		List<Attribute> toRemove = new ArrayList<>();
		for(Attribute a : this.attributes){
			if(a instanceof Facet && ((Facet) a).getEntities().isEmpty()){
				toRemove.add(a);
			}
		}
		for(Attribute a : toRemove){
			this.attributes.remove(a);
		}
	}
	public Set<Attribute> getAttributes() {
		return attributes;
	}
	
	public int getAttributeNb() {
		return attributes.size();
	}
	
	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	
	public Set<Entity> getEntities(Attribute a) {
		if ( reverseRelation.containsKey(a) )
			return reverseRelation.get(a);
		else
			return new HashSet<Entity>();
	}
	
	public Set<Entity> getEntities() {
		return entities;
	}
	
	public int getEntityNb() {
		return entities.size();
	}

	public Map<Entity, Set<Attribute>> getRelation() {
		return relation;
	}

	public Map<Attribute, Set<Entity>> getReverseRelation() {
		return reverseRelation;
	}

	public String toString() {
		return this.getClass().getSimpleName() + " with " + getEntityNb() +
		" entities, " + getAttributeNb() + " attributes and " +
		getPairNb() + " pairs in the binary relation.";
	}

	public Entity getEntity(String name){
		for(Entity e : this.entities){
			if(e.getName().equals(name)){
				return e;
			}
		}
		return null;
	}

	public Attribute getFacet(String type, String value){
		for(Attribute a : this.attributes){
			if(a instanceof Facet){
				if(((Facet) a).getType().equals(type) && ((Facet) a).getValue().equals(value)){
					return a;
				}
			}
		}
		return null;
	}


	//added by me
	public Map<Set<Entity>,Set<Attribute>> getPairs(){

		int size = this.getAttributeNb();

		Map<Set<Entity>, Set<Attribute>> pairs = new HashMap<>();

		for(int i = 0; i < (1<<size); i++){
			Set<Attribute> attributeSet = new HashSet<>();
			Set<Entity> entitySet = new HashSet<>();

			for(int j = 0; j < size; j++){
				if((i & (1 << j)) > 0){
					attributeSet.add((Attribute) this.attributes.toArray()[j]);
				}
			}

			int count = 0;
			if(attributeSet.size() > 0 && attributeSet.size() != size) {
				for(Entity e : this.entities){
					if(this.getAttributes(e).containsAll(attributeSet)){
						count++;
						entitySet.add(e);
					}
				}
				if(count > 0) {
						pairs.put(entitySet, attributeSet);

				}
			}
			if(size == 1){
				for(Entity e : this.entities){
					if(this.getAttributes(e).containsAll(attributeSet)){
						count++;
						entitySet.add(e);
					}
				}
				if(count > 0) {
					pairs.put(entitySet, attributeSet);

				}
			}
		}

		return pairs;
	}
}
