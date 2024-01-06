package io.mc.physics;

import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class CollisionManifold {

        private Vector3f overlap;
        private CollisionFace face;
        private boolean didCollide;

        public CollisionManifold(
                        @NonNull Vector3f overlap,
                        @NonNull CollisionFace face,
                        @NonNull boolean didCollide) {
                this.overlap = overlap;
                this.face = face;
                this.didCollide = didCollide;
        }

        /**
         * @return the overlap
         */
        public Vector3f getOverlap() {
                return overlap;
        }

        /**
         * @param overlap the overlap to set
         */
        public void setOverlap(Vector3f overlap) {
                this.overlap = overlap;
        }

        /**
         * @return the face
         */
        public CollisionFace getFace() {
                return face;
        }

        /**
         * @param face the face to set
         */
        public void setFace(CollisionFace face) {
                this.face = face;
        }

        /**
         * @return the didCollide
         */
        public boolean isDidCollide() {
                return didCollide;
        }

        /**
         * @param didCollide the didCollide to set
         */
        public void setDidCollide(boolean didCollide) {
                this.didCollide = didCollide;
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((overlap == null) ? 0 : overlap.hashCode());
                result = prime * result + ((face == null) ? 0 : face.hashCode());
                result = prime * result + (didCollide ? 1231 : 1237);
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (!(obj instanceof CollisionManifold))
                        return false;
                CollisionManifold other = (CollisionManifold) obj;
                if (overlap == null) {
                        if (other.overlap != null)
                                return false;
                } else if (!overlap.equals(other.overlap))
                        return false;
                if (face != other.face)
                        return false;
                if (didCollide != other.didCollide)
                        return false;
                return true;
        }

        @Override
        public String toString() {
                return "CollisionManifold [overlap=" + overlap + ", face=" + face + ", didCollide=" + didCollide + "]";
        }

        
}