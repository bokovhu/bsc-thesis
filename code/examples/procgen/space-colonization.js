function Node(parent) {

    this.pos = [0, 0, 0];
    this.parent = parent;
    this.children = [];
    this.depth = parent ? parent.depth + 1 : 1;

}

function SpaceColonizer(root, attractors, opts) {

    this.root = root;
    this.attractors = attractors;
    this.nodes = [root];
    this.opts = {
        maxBranchLength: (opts && opts.maxBranchLength) || 1,
        maxAttractionDistance: (opts && opts.maxAttractionDistance) || 0.5,
        killDistance: (opts && opts.killDistance) || 0.2
    }

}

SpaceColonizer.prototype.solve = function () {

    while (this.attractors.length > 0) {

        let best = undefined;
        for (let node of this.nodes) {

            for (let attr of this.attractors) {
                let leafAttrDistance = distance(attr, node.pos);
                if (!best || leafAttrDistance < best[0]) {
                    best = [leafAttrDistance, node, attr];
                }
            }

        }

        if (!best) {
            throw new Error('Could not find best, but there are remaining attractors!');
        }

        const [bestDistance, bestNode, closestAttractor] = best;
        let pullDir = [0, 0, 0];

        for(let attr of this.attractors) {

            let d = distance(bestNode.pos, attr);
            if (d < this.opts.maxAttractionDistance) {
                let to = normalize(sub(attr, bestNode.pos));
                pullDir = add(pullDir, to);
            }

        }

        if(length(pullDir) > 0.5) {
            pullDir = normalize(pullDir);
        } else {
            let meanPullPos = [closestAttractor[0], closestAttractor[1], closestAttractor[2]];
            pullDir = normalize(sub(meanPullPos, bestNode.pos));
        }

        pullDir = mul(pullDir, Math.max(0.01, Math.min(this.opts.maxBranchLength, length(sub(bestNode.pos, closestAttractor)))));

        let newNode = new Node(bestNode);
        bestNode.children.push(newNode);
        newNode.pos = add(bestNode.pos, pullDir);

        this.nodes.push(newNode);

        let removeAttractors = [];
        for (let node of this.nodes) {
            for (let attr of this.attractors) {

                let d = Math.abs(distance(attr, node.pos));
                if (d < this.opts.killDistance) {
                    removeAttractors.push(attr);
                }

            }
        }

        this.attractors = this.attractors.filter(
            a => removeAttractors.filter(r => length(sub(r, a)) < 0.01).length <= 0
        );

    }

}

function distance(p1, p2) {
    let d = [
        (p1[0] - p2[0]),
        (p1[1] - p2[1]),
        (p1[2] - p2[2])
    ];
    return Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
}

function length(a) {
    let d = [
        a[0] * a[0],
        a[1] * a[1],
        a[2] * a[2]
    ];
    return Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
}

function add(a, b) {
    return [a[0] + b[0], a[1] + b[1], a[2] + b[2]];
}

function sub(a, b) {
    return [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
}

function mul(a, scalar) {
    return [a[0] * scalar, a[1] * scalar, a[2] * scalar];
}

function normalize(a) {
    let l = length(a);
    return [a[0] / l, a[1] / l, a[2] / l];
}

const NUM_ATTRACTORS = 20;
const ATTR_MINX = -3;
const ATTR_MAXX = 3;
const ATTR_MINY = 3;
const ATTR_MAXY = 25;
const ATTR_MINZ = -3;
const ATTR_MAXZ = 3;

const MIN_WIDTH = 0.25;
const MAX_WIDTH = 2;

var root = new Node(null);
var points = [];
for (let i = 0; i < NUM_ATTRACTORS; i++) {

    let quadrant = Math.random() < 0.5 ? Math.random() > 0.5 ? 1 : 2 : Math.random() < 0.5 ? 3 : 4;

    let x = ATTR_MINX + (ATTR_MAXX - ATTR_MINX) * Math.random() + (quadrant === 1 ? -4 : 0) + (quadrant === 3 ? 4 : 0);
    let y = ATTR_MINY + (ATTR_MAXY - ATTR_MINY) * Math.random();
    let z = ATTR_MINZ + (ATTR_MAXZ - ATTR_MINZ) * Math.random() + (quadrant === 2 ? -4 : 0) + (quadrant === 4 ? 4 : 0);

    points.push([x, y, z]);

}

let solver = new SpaceColonizer(root, points, {
    maxAttractionDistance: 1,
    maxBranchLength: 4
});
solver.solve();

let maxDepth = solver.nodes[0].depth;
for (let n of solver.nodes) {
    if (n.depth > maxDepth) maxDepth = n.depth;
}

for (let node of solver.nodes.filter(n => !!n.parent)) {

    let nodeDAlpha = Math.pow((maxDepth - node.depth) / maxDepth, 4.0);
    let radius = MIN_WIDTH + (MAX_WIDTH - MIN_WIDTH) * nodeDAlpha;

    console.log(`capsule_line (a: (${node.pos[0]}, ${node.pos[1]}, ${node.pos[2]}), b: (${node.parent.pos[0]}, ${node.parent.pos[1]}, ${node.parent.pos[2]}), radius: ${radius})`)

}
