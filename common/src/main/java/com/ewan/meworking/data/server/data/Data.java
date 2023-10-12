package com.ewan.meworking.data.server.data;

/**
 * Pure data, without context.
 * RULE 1: ALL FIELDS MUST BE FINAL
 * RULE 2: ONLY ONE ALL-ARG CONSTRUCTOR ALLOWED
 * RULE 3: DIRECT EXTENSIONS OF 'Data' ARE TO BE ABSTRACT, REPRESENTING THE 'CATEGORY' OF DATA, DEFINED BY ITS CONTEXT (e.g entity-specific data all extends EntityData which extends Data).
 */
public abstract class Data {

}
