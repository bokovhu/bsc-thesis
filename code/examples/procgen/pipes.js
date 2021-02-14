const start = `light ambient {
    energy: (0.2, 0.2, 0.2)
}

light directional {
    energy: (1, 1, 1),
    direction: (0.4575, 0.7625, -0.4575)
}

material constant {
    boundary: EVERYWHERE ,
    diffuse: (1, 1, 1),
    shininess: 80
}

prefab container_small{
    CYLINDER (
        radius: 0.5,
        height: 1
    )
}

prefab container_large{
    CYLINDER (
        radius: 1,
        height: 1
    )
}

prefab pipe_turn{
    INTERSECT [
        TORUS (radius: (1, 0.2)),
        BOX  AT  POSITION (-1, 0, 1)(bounds: (2, 2, 2))
    ]
}

prefab pipe{
    CYLINDER (
        radius: 0.2,
        height: 1
    )
}
`;

const w = 8;
const h = 8;
const d = 8;
const level = new Uint32Array(w * h * d);
const clamp = (a, min, max) => a < min ? min : a > max ? max : a;
const clamp3 = (a, min, max) => [clamp(a[0], min[0], max[0]), clamp(a[1], min[1], max[1]), clamp(a[2], min[2], max[2])]
const add3 = (a, b) => [a[0] + b[0], a[1] + b[1], a[2] + b[2]];

const choices = ["fw", "turn"];
const choose = () => choices[Math.floor(choices.length * Math.random())]

const getAt = (pos) => level[clamp(pos[2], 0, d - 1) * w * h + clamp(pos[1], 0, h - 1) * w + clamp(pos[0], 0, w - 1)];
const setAt = (pos, v) => level[clamp(pos[2], 0, d - 1) * w * h + clamp(pos[1], 0, h - 1) * w + clamp(pos[0], 0, w - 1)] = v;

const dirs = [
    [1, 0, 0],
    [-1, 0, 0],
    [0, 1, 0],
    [0, -1, 0],
    [0, 0, 1],
    [0, 0, -1]
]

for(let i = 0; i < 10; i++) {

    let dir = dirs[Math.floor(dirs.length * Math.random())];
    let pos = [
        Math.floor(w * Math.random()),
        Math.floor(h * Math.random()),
        Math.floor(d * Math.random())
    ];

    while (true) {

        const curr = getAt(pos);

        if (curr !== 0) {
            break;
        }

        const choice = choose();

        switch (choice) {
            case "fw":

                const newPos = clamp3(add3(pos, dir), [0, 0, 0], [w - 1, h - 1, d - 1]);
                const next = getAt(newPos);
                if (next === 0) {

                    const forwardDirection =
                        (Math.floor(Math.abs(dir[0])))
                        | (Math.floor(Math.abs(dir[1])) << 1)
                        | (Math.floor(Math.abs(dir[2])) << 2);

                    setAt(pos, forwardDirection);
                    pos = clamp3(add3(pos, dir), [0, 0, 0], [w - 1, h - 1, d - 1]);
                }

                break;
            case "turn":

                const newDir = dirs[Math.floor(dirs.length * Math.random())];
                const nextPosWithNewDir = clamp3(add3(pos, newDir), [0, 0, 0], [w - 1, h - 1, d - 1]);
                const nextWithNewDir = getAt(newDir);
                if (nextWithNewDir === 0) {

                    const turnDirection =
                        (1 << 3)
                        | ((dir[0] === -1 ? 1 : 0) << 4)
                        | ((dir[0] === 1 ? 1 : 0) << 5)
                        | ((dir[1] === -1 ? 1 : 0) << 6)
                        | ((dir[1] === 1 ? 1 : 0) << 7)
                        | ((dir[2] === -1 ? 1 : 0) << 8)
                        | ((dir[2] === 1 ? 1 : 0) << 9)

                        | ((newDir[0] === -1 ? 1 : 0) << 10)
                        | ((newDir[0] === 1 ? 1 : 0) << 11)
                        | ((newDir[1] === -1 ? 1 : 0) << 12)
                        | ((newDir[1] === 1 ? 1 : 0) << 13)
                        | ((newDir[2] === -1 ? 1 : 0) << 14)
                        | ((newDir[2] === 1 ? 1 : 0) << 15);

                    setAt(pos, turnDirection);
                    dir = newDir;
                    pos = clamp3(add3(pos, dir), [0, 0, 0], [w - 1, h - 1, d - 1]);
                }

                break;
        }

    }

}

for(let z = 0; z < d; z++) {
    for(let y = 0; y < h; y++) {
        for(let x = 0; x < w; x++) {

            const v = getAt([x, y, z]);
            if(v === 0) {
                continue;
            }

            const bin = v.toString("2");

            if((v & (1 << 3)) !== 0) {
                console.log(`Turn: ${'0000000000000000'.substr(bin.length)}${bin}`);

                const xzTorus = (
                    ((v[4] | v[5]) & (v[14] | v[15]))
                    | ((v[8] | v[9]) & (v[10] | v[11]))
                ) !== 0;

                if (xzTorus) {

                }

            } else {
                console.log(`Forward: ${'0000000000000000'.substr(bin.length)}${bin}`);
            }

        }
    }
}