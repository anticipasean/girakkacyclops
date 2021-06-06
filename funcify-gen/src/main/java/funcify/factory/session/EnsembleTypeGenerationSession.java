package funcify.factory.session;

import funcify.ensemble.EnsembleKind;
import funcify.factory.session.EnsembleTypeGenerationSession.EnsembleTypeGenerationSessionWT;
import funcify.template.session.TypeGenerationSession;
import funcify.tool.container.SyncMap;

/**
 * @author smccarron
 * @created 2021-05-29
 */
public interface EnsembleTypeGenerationSession<TD, MD, CD, SD, ED> extends
                                                                   TypeGenerationSession<EnsembleTypeGenerationSessionWT, TD, MD, CD, SD, ED> {

    /**
     * Session Witness Type for Higher Kinded Typing
     */
    public static enum EnsembleTypeGenerationSessionWT {

    }

    static <TD, MD, CD, SD, ED> EnsembleTypeGenerationSession<TD, MD, CD, SD, ED> narrowK(final TypeGenerationSession<EnsembleTypeGenerationSessionWT, TD, MD, CD, SD, ED> typeGenerationSession) {
        return (EnsembleTypeGenerationSession<TD, MD, CD, SD, ED>) typeGenerationSession;
    }

    TD getBaseEnsembleInterfaceTypeDefinition();

    SyncMap<EnsembleKind, TD> getEnsembleInterfaceTypeDefinitionsByEnsembleKind();

}