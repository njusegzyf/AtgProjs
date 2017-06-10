/*
* Raytrace.cpp
*
*  Author: Zhang Yifan
*/

#include <malloc.h>

#include <iostream>
#include <vector>
#include <limits>
#include <iomanip>
#include <fstream>
#include <cstring>
#include <cmath>
#include <cfloat>

// Code for raytrace

// use unnamed (anonymous) namespace to avoid conflict
// @see http://en.cppreference.com/w/cpp/language/namespace#Unnamed_namespaces
// namespace {

//void skip() {}
#define skip()

#pragma region Simple classes

class Color final {
public:
  float r_, g_, b_; // use tail underscore for class members

  Color(float r, float g, float b) :
    r_(r), g_(g), b_(b) {
    //    r_ = r;
    //    g_ = g;
    //    b_ = b;
  }

  float getRed() const {
    return r_;
  }
  float getGreen() const {
    return g_;
  }
  float getBlue() const {
    return b_;
  }
};

// A simple vector class
class Vector3D final {
public:
  float x_, y_, z_;

  // constructors
  Vector3D(float x, float y, float z) :
    x_(x), y_(y), z_(z) {
  }

  Vector3D() :
    Vector3D(0.0, 0.0, 0.0) {
  }

  Vector3D(const Vector3D& v) :
    Vector3D(v.x_, v.y_, v.z_) {
  }

  // methods
  float dot(const Vector3D& B) const {
    return x_ * B.x_ + y_ * B.y_ + z_ * B.z_;
  }

  float dot(float Bx, float By, float Bz) const {
    return x_ * Bx + y_ * By + z_ * Bz;
  }

  static float dot(const Vector3D& A, const Vector3D& B) {
    return A.x_ * B.x_ + A.y_ * B.y_ + A.z_ * B.z_;
  }

  Vector3D cross(const Vector3D& B) const {
    return Vector3D(y_ * B.z_ - z_ * B.y_, z_ * B.x_ - x_ * B.z_, x_ * B.y_ - y_ * B.x_);
  }

  Vector3D cross(float Bx, float By, float Bz) const {
    return Vector3D(y_ * Bz - z_ * By, z_ * Bx - x_ * Bz, x_ * By - y_ * Bx);
  }

  static Vector3D cross(const Vector3D& A, const Vector3D& B) {
    return Vector3D(A.y_ * B.z_ - A.z_ * B.y_, A.z_ * B.x_ - A.x_ * B.z_, A.x_ * B.y_ - A.y_ * B.x_);
  }

  float length() const {
    return (float)std::sqrt(x_ * x_ + y_ * y_ + z_ * z_);
  }

  static float length(const Vector3D& a) {
    // return (float) Math.sqrt(A.x * A.x + A.y * A.y + A.z * A.z);
    return a.length();
  }

  void normalize() {
    float t = x_ * x_ + y_ * y_ + z_ * z_;
    if (t != 0 && t != 1) {
      t = (float)(1.0 / std::sqrt(t));
    } else {
      skip(); // `skip()` is added to empty else branches
    }
    x_ *= t;
    y_ *= t;
    z_ *= t;
  }

  static Vector3D normalize(const Vector3D& a) {
    //    float t = A.x * A.x + A.y * A.y + A.z * A.z;
    //    if (t != 0 && t != 1)
    //      t = (float) (1 / Math.sqrt(t));
    //    return new Vector3D(A.x * t, A.y * t, A.z * t);

    Vector3D tempVector3D(a);
    tempVector3D.normalize();
    return tempVector3D;
  }

  //  String toString() {
  //    return new String("[" + x + ", " + y + ", " + z + "]");
  //  }
};

// The following regular functions is converted from member or static function as the tool do not support class.
// same as `Vector3D::Normalize`
void Vector3D_Normalize(Vector3D& v) {
  float t = v.x_ * v.x_ + v.y_ * v.y_ + v.z_ * v.z_;
  if (t != 0 && t != 1) {
    t = (float)(1.0 / std::sqrt(t));
  } else {
    skip();
  }
  v.x_ *= t;
  v.y_ *= t;
  v.z_ *= t;
}

// All the  variables here are ugly, but I wanted Lights and Surfaces to be "friends"
class Light {
public:
  static constexpr int AMBIENT = 0;
  static constexpr int DIRECTIONAL = 1;
  static constexpr int POINT = 2;

  int lightType_;
  // the position of a point light or the direction to a directional light
  Vector3D lvec_;
  // intensity of the light source
  float ir_, ig_, ib_;

  Light(int type, const Vector3D& v, float r, float g, float b) :
    lightType_(type), ir_(r), ig_(g), ib_(b) {
    if (type != AMBIENT) {
      lvec_ = v;
      if (type == DIRECTIONAL) {
        lvec_.normalize();
      } else {
        skip();
      }
    } else {
      skip();
    }
  }

  // Note: This default constructor should be used with `Light_Ctor`.
  Light() {
  }
};

// same as `Light`'s constructor
static Light Light_Ctor(int type, const Vector3D& v, float r, float g, float b) {
  Light temp;
  temp.lightType_ = type;
  temp.ir_ = r;
  temp.ig_ = g;
  temp.ib_ = b;

  if (type != Light::AMBIENT) {
    temp.lvec_ = v;
    if (type == Light::DIRECTIONAL) {
      // temp.lvec_.normalize();
      Vector3D_Normalize(temp.lvec_);
    } else {
      skip();
    }
  } else {
    skip();
  }
  return temp;
}

#pragma endregion

#pragma region Forward declarations

class Sphere;
class Ray;

#pragma endregion

#pragma region Class declarations

class Surface final {
public:
  static constexpr float TINY = 0.001f;
  static constexpr float I255 = 0.00392156f;  // 1/255

  float ir_, ig_, ib_;        // surface's intrinsic color
  float ka_, kd_, ks_, ns_;    // constants for phong model
  float kt_, kr_, nt_;

  Surface(float rval, float gval, float bval, float a, float d, float s, float n, float r, float t, float index) :
    ir_(rval), ig_(gval), ib_(bval), ka_(a), kd_(d), ks_(s), ns_(n), kr_(r * I255), kt_(t), nt_(index) {
  }

  Surface() {
  }

  Color Shade(const Vector3D& p, const Vector3D& n, const Vector3D& v, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd);
};

class Sphere /* : public Renderable */final {
public:
  Surface surface_;
  Vector3D center_;
  float radius_;
  float radSqr_;

  Sphere(const Surface& s, const Vector3D& c, float r) :
    surface_(s), center_(c), radius_(r), radSqr_(r * r) {
  }

  Sphere() {
  }

  Sphere& operator=(const Sphere& o) = default;

  bool intersect(Ray& ray) const;
  Color Shade(const Ray& ray, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd);

  //  public String toString() {
  //    return ("sphere " + center + " " + radius);
  //  }
};

// Note: class `Renderable` is omitted for simplification.

// An object must implement a Renderable interface in order to be ray traced.
// Using this interface it is straight to add new objects.
//class Renderable {
//public:
//  virtual bool intersect(const Ray& r) const = 0;
//  virtual Color Shade(const Ray& r, const Vector& lights, const Vector& objects, const Color& bgnd) const = 0;
//  //    public String toString();
//};

// use as `Sphere` as `Renderable`
using Renderable = Sphere;

class Ray final {
public:
  static constexpr float kMaxT = FLT_MAX;
  Vector3D origin_;
  Vector3D direction_;
  float t_;
  Renderable object_;

  Ray(const Vector3D& eye, const Vector3D& dir) :
    origin_(eye), direction_(dir) {
    direction_.normalize(); // direction = Vector3D.normalize(dir);
  }

  bool trace(const std::vector<Renderable>& objects);

  // The following method is not strictly needed, and most likely
  // adds unnecessary overhead, but I prefered the syntax
  //
  //            ray.Shade(...)
  // to
  //            ray.object.Shade(ray, ...)
  //
  Color Shade(const std::vector<Light>& lights, const std::vector<Renderable>& objects, const Color& bgnd) {
    return object_.Shade(*this, lights, objects, bgnd);
  }

  //String toString() {
  //  return ("ray origin = " + origin + "  direction = " + direction + "  t = " + t);
  //}
};

#pragma endregion

#pragma region Class Surface definitions

Color Surface::Shade(const Vector3D& p, const Vector3D& n, const Vector3D& v, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd) {
  float r = 0.0f;
  float g = 0.0f;
  float b = 0.0f;

  for (const Light& light : lights) {
    if (light.lightType_ == Light::AMBIENT) {
      r += ka_ * ir_ * light.ir_;
      g += ka_ * ig_ * light.ig_;
      b += ka_ * ib_ * light.ib_;
    } else {
      Vector3D l;
      if (light.lightType_ == Light::POINT) {
        l = Vector3D(light.lvec_.x_ - p.x_, light.lvec_.y_ - p.y_, light.lvec_.z_ - p.z_);
        l.normalize();
      } else {
        l = Vector3D(-light.lvec_.x_, -light.lvec_.y_, -light.lvec_.z_);
      }

      // Check if the surface point is in shadow
      Vector3D poffset(p.x_ + Surface::TINY * l.x_, p.y_ + Surface::TINY * l.y_, p.z_ + Surface::TINY * l.z_);
      Ray shadowRay(poffset, l);
      if (shadowRay.trace(objects)) {
        break;
      } else {
        skip();
      }

      float lambert = Vector3D::dot(n, l);
      if (lambert > 0) {
        if (kd_ > 0) {
          float diffuse = kd_ * lambert;
          r += diffuse * ir_ * light.ir_;
          g += diffuse * ig_ * light.ig_;
          b += diffuse * ib_ * light.ib_;
        } else {
          skip();
        }

        if (ks_ > 0) {
          lambert *= 2;
          float spec = v.dot(lambert * n.x_ - l.x_, lambert * n.y_ - l.y_, lambert * n.z_ - l.z_);
          if (spec > 0) {
            spec = ks_ * std::pow(spec, ns_);
            r += spec * light.ir_;
            g += spec * light.ig_;
            b += spec * light.ib_;
          } else {
            skip();
          }
        } else {
          skip();
        }
      }
    }
  }

  // Compute illumination due to reflection
  if (kr_ > 0) {
    float t = v.dot(n);
    if (t > 0) {
      t *= 2;
      Vector3D reflect(t * n.x_ - v.x_, t * n.y_ - v.y_, t * n.z_ - v.z_);
      Vector3D poffset(p.x_ + Surface::TINY * reflect.x_, p.y_ + Surface::TINY * reflect.y_, p.z_ + Surface::TINY * reflect.z_);
      Ray reflectedRay(poffset, reflect);
      if (reflectedRay.trace(objects)) {
        Color rcolor = reflectedRay.Shade(lights, objects, bgnd);
        r += kr_ * rcolor.getRed();
        g += kr_ * rcolor.getGreen();
        b += kr_ * rcolor.getBlue();
      } else {
        r += kr_ * bgnd.getRed();
        g += kr_ * bgnd.getGreen();
        b += kr_ * bgnd.getBlue();
      }
    } else {
      skip();
    }
  } else {
    skip();
  }

  // Add code for refraction here
  //r = (r > 1.0f) ? 1.0f : r;
  //g = (g > 1.0f) ? 1.0f : g;
  //b = (b > 1.0f) ? 1.0f : b;
  if (r > 1.0f) {
    r = 1.0f;
  } else {
    skip(); // r = r;
  }
  if (g > 1.0f) {
    g = 1.0f;
  } else {
    skip(); // g = g;
  }
  if (b > 1.0f) {
    b = 1.0f;
  } else {
    skip(); // b = b;
  }
  return Color(r, g, b);
}

// same as `Surface::Shade`
static Color Surface_Shade(Surface& self,
                           const Vector3D& p, const Vector3D& n, const Vector3D& v, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd) {
  float r = 0.0f;
  float g = 0.0f;
  float b = 0.0f;

  for (const Light& light : lights) {
    if (light.lightType_ == Light::AMBIENT) {
      r += self.ka_ * self.ir_ * light.ir_;
      g += self.ka_ * self.ig_ * light.ig_;
      b += self.ka_ * self.ib_ * light.ib_;
    } else {
      Vector3D l;
      if (light.lightType_ == Light::POINT) {
        l = Vector3D(light.lvec_.x_ - p.x_, light.lvec_.y_ - p.y_, light.lvec_.z_ - p.z_);
        l.normalize();
      } else {
        l = Vector3D(-light.lvec_.x_, -light.lvec_.y_, -light.lvec_.z_);
      }

      // Check if the surface point is in shadow
      Vector3D poffset(p.x_ + Surface::TINY * l.x_, p.y_ + Surface::TINY * l.y_, p.z_ + Surface::TINY * l.z_);
      Ray shadowRay(poffset, l);
      bool tempRes = shadowRay.trace(objects);
      if (tempRes == true) {
        break;
      } else {
        skip();
      }

      float lambert = Vector3D::dot(n, l);
      if (lambert > 0) {
        if (self.kd_ > 0) {
          float diffuse = self.kd_ * lambert;
          r += diffuse * self.ir_ * light.ir_;
          g += diffuse * self.ig_ * light.ig_;
          b += diffuse * self.ib_ * light.ib_;
        } else {
          skip();
        }

        if (self.ks_ > 0) {
          lambert *= 2;
          float spec = v.dot(lambert * n.x_ - l.x_, lambert * n.y_ - l.y_, lambert * n.z_ - l.z_);
          if (spec > 0) {
            spec = self.ks_ * std::pow(spec, self.ns_);
            r += spec * light.ir_;
            g += spec * light.ig_;
            b += spec * light.ib_;
          } else {
            skip();
          }
        } else {
          skip();
        }
      }
    }
  }

  // Compute illumination due to reflection
  if (self.kr_ > 0) {
    float t = v.dot(n);
    if (t > 0) {
      t *= 2;
      Vector3D reflect(t * n.x_ - v.x_, t * n.y_ - v.y_, t * n.z_ - v.z_);
      Vector3D poffset(p.x_ + Surface::TINY * reflect.x_, p.y_ + Surface::TINY * reflect.y_, p.z_ + Surface::TINY * reflect.z_);
      Ray reflectedRay(poffset, reflect);
      bool tempRes = reflectedRay.trace(objects);
      if (tempRes == true) {
        Color rcolor = reflectedRay.Shade(lights, objects, bgnd);
        r += self.kr_ * rcolor.getRed();
        g += self.kr_ * rcolor.getGreen();
        b += self.kr_ * rcolor.getBlue();
      } else {
        r += self.kr_ * bgnd.getRed();
        g += self.kr_ * bgnd.getGreen();
        b += self.kr_ * bgnd.getBlue();
      }
    } else {
      skip();
    }
  } else {
    skip();
  }

  // Add code for refraction here
  //r = (r > 1.0f) ? 1.0f : r;
  //g = (g > 1.0f) ? 1.0f : g;
  //b = (b > 1.0f) ? 1.0f : b;
  if (r > 1.0f) {
    r = 1.0f;
  } else {
    skip(); // r = r;
  }
  if (g > 1.0f) {
    g = 1.0f;
  } else {
    skip(); // g = g;
  }
  if (b > 1.0f) {
    b = 1.0f;
  } else {
    skip(); // b = b;
  }
  return Color(r, g, b);
}

#pragma endregion

#pragma region Class Sphere definitions

bool Sphere::intersect(Ray& ray) const {
  float dx = center_.x_ - ray.origin_.x_;
  float dy = center_.y_ - ray.origin_.y_;
  float dz = center_.z_ - ray.origin_.z_;
  float v = ray.direction_.dot(dx, dy, dz);

  // Do the following quick check to see if there is even a chance
  // that an intersection here might be closer than a previous one
  if (v - radius_ > ray.t_) {
    return false;
  } else {
    skip();
  }

  // Test if the ray actually intersects the sphere
  float t = radSqr_ + v * v - dx * dx - dy * dy - dz * dz;
  if (t < 0) {
    return false;
  } else {
    skip();
  }

  // Test if the intersection is in the positive
  // ray direction and it is the closest so far
  t = v - (float)std::sqrt(t);
  if ((t > ray.t_) || (t < 0)) {
    return false;
  } else {
    skip();
  }

  ray.t_ = t;
  ray.object_ = *this;
  return true;
}

// same as `Sphere::intersect`
static bool Sphere_intersect(const Sphere& self, Ray& ray) {
  float dx = self.center_.x_ - ray.origin_.x_;
  float dy = self.center_.y_ - ray.origin_.y_;
  float dz = self.center_.z_ - ray.origin_.z_;
  float v = ray.direction_.dot(dx, dy, dz);

  // Do the following quick check to see if there is even a chance
  // that an intersection here might be closer than a previous one
  if (v - self.radius_ > ray.t_) {
    return false;
  } else {
    skip();
  }

  // Test if the ray actually intersects the sphere
  float t = self.radSqr_ + v * v - dx * dx - dy * dy - dz * dz;
  if (t < 0) {
    return false;
  } else {
    skip();
  }

  // Test if the intersection is in the positive
  // ray direction and it is the closest so far
  t = v - (float)std::sqrt(t);
  if ((t > ray.t_) || (t < 0)) {
    return false;
  } else {
    skip();
  }

  ray.t_ = t;
  ray.object_ = self;
  return true;
}

Color Sphere::Shade(const Ray& ray, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd) {
  // An object shader doesn't really do too much other than
  // supply a few critical bits of geometric information
  // for a surface shader. It must must compute:
  //
  //   1. the point of intersection (p)
  //   2. a unit-length surface normal (n)
  //   3. a unit-length vector towards the ray's origin (v)
  //
  float px = ray.origin_.x_ + ray.t_ * ray.direction_.x_;
  float py = ray.origin_.y_ + ray.t_ * ray.direction_.y_;
  float pz = ray.origin_.z_ + ray.t_ * ray.direction_.z_;

  Vector3D p(px, py, pz);
  Vector3D v(-ray.direction_.x_, -ray.direction_.y_, -ray.direction_.z_);
  Vector3D n(px - center_.x_, py - center_.y_, pz - center_.z_);
  n.normalize();

  // The illumination model is applied by the surface's Shade() method
  return surface_.Shade(p, n, v, lights, objects, bgnd);
}

// same as `Color Sphere_Shade`
static Color Sphere_Shade(Sphere& self, const Ray& ray, const std::vector<Light>& lights, const std::vector<Sphere>& objects, const Color& bgnd) {
  // An object shader doesn't really do too much other than
  // supply a few critical bits of geometric information
  // for a surface shader. It must must compute:
  //
  //   1. the point of intersection (p)
  //   2. a unit-length surface normal (n)
  //   3. a unit-length vector towards the ray's origin (v)
  //
  float px = ray.origin_.x_ + ray.t_ * ray.direction_.x_;
  float py = ray.origin_.y_ + ray.t_ * ray.direction_.y_;
  float pz = ray.origin_.z_ + ray.t_ * ray.direction_.z_;

  Vector3D p(px, py, pz);
  Vector3D v(-ray.direction_.x_, -ray.direction_.y_, -ray.direction_.z_);
  Vector3D n(px - self.center_.x_, py - self.center_.y_, pz - self.center_.z_);
  n.normalize();

  // The illumination model is applied by the surface's Shade() method
  return Surface_Shade(self.surface_, p, n, v, lights, objects, bgnd);
}

#pragma endregion

#pragma region Class Ray definitions

bool Ray::trace(const std::vector<Renderable>& objects) {
  if (objects.empty()) {
    return false;
  }

  t_ = Ray::kMaxT;
  for (const auto& obj : objects) {
    object_ = obj;
    object_.intersect(*this);
  }
  return true;

  //Enumeration objList = objects.elements();
  //t = MAX_T;
  //object = null;
  //while (objList.hasMoreElements()) {
  //  Renderable object = (Renderable)objList.nextElement();
  //  object.intersect(this);
  //}
  //return (object != null);
}

// same as "Ray::trace"
static bool Ray_trace(Ray& self, const std::vector<Renderable>& objects) {
  bool tempRes = objects.empty();
  if (tempRes == true) {
    return false;
  }

  self.t_ = Ray::kMaxT;
  for (const auto& obj : objects) {
    self.object_ = obj;
    self.object_.intersect(self);
  }
  return true;

  //Enumeration objList = objects.elements();
  //t = MAX_T;
  //object = null;
  //while (objList.hasMoreElements()) {
  //  Renderable object = (Renderable)objList.nextElement();
  //  object.intersect(this);
  //}
  //return (object != null);
}

#pragma endregion

// }

// Code for raytrace test methods

void vector3DNormalize(float x, float y, float z) {
  // Vector3D(x, y, z).normalize();
  Vector3D v(x, y, z);
  Vector3D_Normalize(v);
}

void surfaceShade(float rval, float gval, float bval, float a, float d, float s, float n, float r, float t,
                  float index, float pX, float pY, float pZ, float nX, float nY, float nZ, float vX, float vY, float vZ,
                  int lType, float lX, float lY, float lZ, float lR, float lG, float lB) {
  Surface surface(rval, gval, bval, a, d, s, n, r, t, index);
  Vector3D pVec(pX, pY, pZ);
  Vector3D nVec(nX, nY, nZ);
  Vector3D vVec(vX, vY, vZ);

  std::vector<Light> lights;
  // Light l(lType, Vector3D(lX, lY, lZ), lR, lG, lB);
  // lights.push_back(l);
  lights.emplace_back(lType, Vector3D(lX, lY, lZ), lR, lG, lB);

  // surface.Shade(pVec, nVec, vVec, lights, std::vector<Renderable>(), Color(1, 1, 1));
  Surface_Shade(surface, pVec, nVec, vVec, lights, std::vector<Renderable>(), Color(1, 1, 1));
}

void rayTrace(float cX, float cY, float cZ, float r, float eyeX, float eyeY, float eyeZ, float dirX, float dirY, float dirZ) {
  // Sphere.intersect() does not use the {@code surface} field.
  std::vector<Renderable> objects;
  // Sphere sphere(nullptr, Vector3D(cX, cY, cZ), r);
  // objects.add(sphere);
  objects.emplace_back(Surface(), Vector3D(cX, cY, cZ), r);

  Vector3D eye(eyeX, eyeY, eyeZ);
  Vector3D dir(dirX, dirY, dirZ);
  // Ray(eye, dir).trace(objects);
  Ray_trace(Ray(eye, dir), objects);
}

void sphereIntersect(float rval, float gval, float bval, float a, float d, float s, float n, float r, float t,
                     float index, float x, float y, float z, float rad, float eyeX, float eyeY, float eyeZ, float dirX, float dirY, float dirZ) {
  Vector3D eye(eyeX, eyeY, eyeZ);
  Vector3D dir(dirX, dirY, dirZ);
  Ray ray(eye, dir);

  Surface surface(rval, gval, bval, a, d, s, n, r, t, index);
  Vector3D center(x, y, z);
  Sphere sphere(surface, center, rad);

  // sphere.intersect(ray);
  Sphere_intersect(sphere, ray);
}

void sphereShade(float rval, float gval, float bval, float a, float d, float s, float n, float r, float t,
                 float index, float x, float y, float z, float rad, float eyeX, float eyeY, float eyeZ,
                 float dirX, float dirY, float dirZ, int lType, float lX, float lY, float lZ, float lR, float lG, float lB, float bgR, float bgG, float bgB) {
  Vector3D eye(eyeX, eyeY, eyeZ);
  Vector3D dir(dirX, dirY, dirZ);
  Ray ray(eye, dir);

  Surface surface(rval, gval, bval, a, d, s, n, r, t, index);
  Vector3D center(x, y, z);
  std::vector<Renderable> objects;

  // FIXME In Java, `sphere` and the one in `objects` points to a same instance, while in this C++ version they are two instance with same init value.
  // Checks whether this makes a difference.
  Sphere sphere(surface, center, rad);
  objects.push_back(sphere);

  std::vector<Light> lights;
  // Light light(lType, Vector3D(lX, lY, lZ), lR, lG, lB);
  // lights.add(light);
  lights.emplace_back(lType, Vector3D(lX, lY, lZ), lR, lG, lB);

  Color bgnd(bgR, bgG, bgB);
  // sphere.Shade(ray, lights, objects, bgnd);
  Sphere_Shade(sphere, ray, lights, objects, bgnd);
}
