package me.bokov.bsc.surfaceviewer.surfacelang;

public class SurfaceLangVisitorImpl extends SurfaceLangBaseListener {

    private final SurfaceLangBuilder builder;

    public SurfaceLangVisitorImpl(SurfaceLangBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    public void exitConstantDeclarationStatement(SurfaceLangParser.ConstantDeclarationStatementContext ctx) {
        super.exitConstantDeclarationStatement(ctx);

        final var typeName = ctx.constantType().IDENTIFIER().getText();
        final var type = SurfaceLangPrimitive.primitiveForName(typeName);
        if (type == null) {
            throw new IllegalStateException("'" + typeName + "' is not a valid primitive type!");
        }

        builder.putConstant(
                new ScopedVariable(
                        type,
                        ctx.constantName().IDENTIFIER().getText()
                )
        );
    }

}
