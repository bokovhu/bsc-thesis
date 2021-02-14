layout(std430, binding = 0) writeonly buffer out_output { OutputVertex[] out_vertices; };
layout(std430, binding = 1) buffer out_vertexCountBuffer { int out_vertexCount; };
uniform sampler3D in_positionAndValue;
uniform sampler3D in_normal;
layout(std430, binding = 2) readonly buffer in_triangleTable { int triangleTableData []; };
layout(std430, binding = 3) readonly buffer in_edgeTable { uint edgeTableData []; };
uniform float u_isoLevel = 0.0;
uniform int u_storageType = 0;
uniform int u_voxelCount = 0;