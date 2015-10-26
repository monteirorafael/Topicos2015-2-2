package br.grupointegrado.ads.flappyBird;
/**
 * Created by Rafael on 26/10/2015.
 */
public class Obstaculo {
    private World mundo;
    private OrtographicCamera camera;
    private Body corpoCima, corpoBaixo;
    private float posX;
    private float posYCima, posYBaixo;

    private float largura, altura;
    private boolean passou;

    private Obstaculo ultimoObstaculo; //ultimo antes do atual

    public Obstaculo(World mundo, OrtographicCamera camera, Obstaculo ultimoObstaculo){
        this.mundo = mundo;
        this.camera = camera;
        this.ultimoObstaculo = ultimoObstaculo;

        initPosicao();
        initCorpoCima();
        initCorpoBaixo();
    }
    private initCorpoBaixo(){
        corpoBaixo = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, posX, posYBaixo);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura / 2);

        Util.criarForma(corpoBaixo, shape, "OBSTACULO_BAIXO");
        shape.dispose();
    }



    private initCorpoCima(){
        corpoCima = Util.criarCorpo(mundo, BodyDef.BodyType.staticBody, posX, posYCima);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura /2);

        Util.criarForma(corpoCima, shape, "OBSTACULO_CIMA");

        shape.dispose;
    }


    private void initPosicao(){
        largura = 40 / Util.PIXEL_METRO;
        altura = camera.viewportHeight / Util.PIXEL_METRO;

        float xInicial = largura;
        if (ultimoObstaculo != null){
            xInicial = ultimoObstaculo.getPosX();
        }
        posX = xInicial + 8 ;

        float parcela = (altura - ALTURA_CHAO) /6;

        int multiplicador = MathUtils.random(1, 3); // numero aleatorio entre 1 e 3

        posYBaixo = ALTURA_CHAO + (parcela * multiplicador) - (altura /2);

        posYCima = posYBaixo + altura + 2f;
    }

    public float getPosX(){


        return this.posX;
    }

    public void remover(){
        mundo.destroyBody(corpoCima);
        mundo.destroyBody(corpoBaixo);
    }

}
