const start = `light ambient {
    energy: (0.04, 0.04, 0.04)
}

light directional {
    energy: (0.55, 0.52, 0.34),
    direction: (0.2968, 0.9076, -0.2968)
}

resource texture stoneDiffuse("assets/stone-diffuse.jpg")
resource texture dirtDiffuse("assets/dirt-diffuse.png")
material triplanar {
    boundary: PLANE (
        h: -0.11,
        normal: (0, 1, 0)
    ),
    diffuseMap: "dirtDiffuse",
    textureScale: 0.2,
    shininess: 3
}

material triplanar {
    boundary: PLANE (
        h: 0.1,
        normal: (0, -1, 0)
    ),
    diffuseMap: "stoneDiffuse",
    textureScale: 0.25,
    shininess: 3
}

material constant {
    boundary: EVERYWHERE ,
    diffuse: (1, 1, 1),
    shininess: 80
}
`;
const prefabs = require("./rooms.prefabs");

const mazeWidth = 8;
const mazeHeight = 8;

let maze = [];

for(let y = 0; y < mazeHeight; y++) {
    let row = [];
    for(let x = 0; x < mazeWidth; x++) {

        const rnd = Math.random();
        const wallIndex = Math.floor(16 * rnd);

        row.push(wallIndex);
    }
    maze.push(row);
}

let mazeLines = [];
for(let y = 0; y < mazeHeight; y++) {
    for(let x = 0; x < mazeWidth; x++) {
        const bits = maze[y][x];
        const bitsText = (bits >>> 0).toString(2);
        mazeLines.push(`room_${'0000'.substr(bitsText.length)}${bitsText} AT POSITION (${x * 2}, 0, ${y * 2})`)
    }
}

console.log(`${start}
${prefabs}
${mazeLines.join("\n")}`)