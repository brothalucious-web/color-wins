#version 150
uniform sampler2D InSampler;
uniform float Transition;
uniform float ColorCount;
uniform float SkyHue;
uniform float TerrainHue;
uniform float EntitiesHue;
uniform float UiHue;
in vec2 texCoord;
out vec4 fragColor;
vec3 rgbToHsv(vec3 c) {
 vec4 K=vec4(0.0,-1.0/3.0,2.0/3.0,-1.0);
 vec4 p=mix(vec4(c.bg,K.wz),vec4(c.gb,K.xy),step(c.b,c.g));
 vec4 q=mix(vec4(p.xyw,c.r),vec4(c.r,p.yzx),step(p.x,c.r));
 float d=q.x-min(q.w,q.y);
 return vec3(abs(q.z+(q.w-q.y)/(6.0*d+1.0e-10)),d/(q.x+1.0e-10),q.x);
}
vec3 hsvToRgb(vec3 c) {
 vec3 p=abs(fract(c.xxx+vec3(0.0,2.0/3.0,1.0/3.0))*6.0-3.0);
 return c.z*mix(vec3(1.0),clamp(p-1.0,0.0,1.0),c.y);
}
vec3 gray(vec3 c) { float l=dot(c,vec3(0.2126,0.7152,0.0722)); return vec3(l); }
vec3 tint(vec3 c,float h) { vec3 v=rgbToHsv(c); v.x=fract(h); v.y=max(v.y,0.72); return hsvToRgb(v); }
void main() {
 vec4 s=texture(InSampler,texCoord); vec3 mono=gray(s.rgb);
 float upper=1.0-smoothstep(0.28,0.62,texCoord.y);
 float worldArea=smoothstep(0.18,0.55,texCoord.y)*(1.0-smoothstep(0.82,0.98,texCoord.y));
 float edgeX=max(1.0-smoothstep(0.02,0.14,texCoord.x),smoothstep(0.86,0.98,texCoord.x));
 float edgeY=max(1.0-smoothstep(0.02,0.13,texCoord.y),smoothstep(0.86,0.98,texCoord.y));
 float hud=max(edgeX,edgeY);
 vec3 outColor=mono;
 outColor=mix(outColor,tint(mono,SkyHue),upper*step(0.5,ColorCount)*Transition);
 outColor=mix(outColor,tint(mono,TerrainHue),worldArea*step(1.5,ColorCount)*Transition);
 outColor=mix(outColor,tint(mono,EntitiesHue),worldArea*step(2.5,ColorCount)*Transition);
 outColor=mix(outColor,tint(mono,UiHue),hud*step(3.5,ColorCount)*Transition);
 fragColor=vec4(outColor,s.a);
}
