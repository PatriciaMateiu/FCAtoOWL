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

public class ConceptLattice extends ConceptOrder {

	protected Concept top;
	
	protected Concept bottom;
	
	public ConceptLattice() {
		super();
	}

	public Concept getTop() {
		return top;
	}

	public void setTop(Concept top) {
		this.top = top;
	}

	public Concept getBottom() {
		return bottom;
	}

	public void setBottom(Concept bottom) {
		this.bottom = bottom;
	}

	//added by me
	public Concept correspondingConcept(String name){
		if(!this.concepts.isEmpty()) {
			for (Concept c : this.concepts) {
				for (Entity e : c.getSimplifiedExtent()) {
					if (e.getName().equals(name)) {
						return c;
					}
				}
			}
		}
		else{
			for(Entity e : this.top.getExtent()){
				if (e.getName().equals(name)) {
					return top;
				}
			}
		}
		return null;
	}
	
}
