package za.co.heritagebaptist.forge.content.admin;
public interface ContentMapper<E,R,D,P> {
    void apply(E entity,R request);
    D admin(E entity);
    P publicDto(E entity);
}
