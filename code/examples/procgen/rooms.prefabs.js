module.exports = `prefab room_0000{
    BOX (bounds: (2, 0.2, 2))
}

prefab room_0001{
    UNION [
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0010{
    UNION [
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0011{
    UNION [
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0100{
    UNION [
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0101{
    UNION [
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0110{
    UNION [
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_0111{
    UNION [
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1000{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1001{
    UNION [
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1010{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1011{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1100{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1101{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1110{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}

prefab room_1111{
    UNION [
        BOX  AT  POSITION (0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, 0.9)(bounds: (2, 2, 0.2)),
        BOX  AT  POSITION (-0.9, 1, 0)(bounds: (0.2, 2, 2)),
        BOX  AT  POSITION (0, 1, -0.9)(bounds: (2, 2, 0.2)),
        BOX (bounds: (2, 0.2, 2))
    ]
}`;